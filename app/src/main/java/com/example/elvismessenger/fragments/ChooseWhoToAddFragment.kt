package com.example.elvismessenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChooseUsersAdapter
import com.example.elvismessenger.adapters.FindUserAdapter
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.launch
import java.util.TreeSet

class ChooseWhoToAddFragment : Fragment(R.layout.fragment_choose_who_to_add) {

    companion object {
        const val GROUP_TO_ADD_MEMBERS = "group_to_add_members"
        const val USER_WHO_ADD_MEMBERS = "user_who_add_members"

        const val RESULT_USER_LIST_CODE = "result_user_list_code"
        const val RESULT_USER_LIST = "result_user_list"
    }

    private lateinit var group: Group
    private lateinit var user: User


    private lateinit var usersToChooseRecyclerView: RecyclerView
    private lateinit var confirmBrn: FloatingActionButton

    private lateinit var adapter: FindUserAdapter
    private val userNotToShowList = TreeSet<User>() // для быстроты поиска
    private val userToShowList = mutableListOf<User>()
    private val chosenUsers = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            it.getParcelable<Group>(GROUP_TO_ADD_MEMBERS)?.let { bundleGroup ->
                group = bundleGroup
            }
            it.getParcelable<User>(USER_WHO_ADD_MEMBERS)?.let {bundleUser ->
                user = bundleUser
            }
        }

        initRecyclerView(view)
        confirmBrn = view.findViewById(R.id.confirm_group_adding)
        confirmBrn.setOnClickListener {
            setFragmentResult(RESULT_USER_LIST_CODE, bundleOf(RESULT_USER_LIST to chosenUsers))
            requireActivity().onBackPressed()
        }
    }

    private fun initRecyclerView(view: View) {

        adapter = ChooseUsersAdapter(userToShowList) {
            if(chosenUsers.contains(it.uid)) {
                chosenUsers.remove(it.uid)
                false
            }
            else {
                chosenUsers.add(it.uid)
                true
            }
        }

        GroupRepository.getGroupUsers(group.id).get().addOnSuccessListener {
            for (i in it.children) {
                val uid = i.getValue(String::class.java)!!
                UserRepository.getInstance().getUserByUID(uid).get()
                    .addOnSuccessListener { userDb ->
                        userNotToShowList.add(userDb.getValue(User::class.java)!!)
                    }
            }

            lifecycleScope.launch {
                usersToChooseRecyclerView = view.findViewById(R.id.users_to_choose_RV)
                usersToChooseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                usersToChooseRecyclerView.adapter = adapter
                UserRepository.getInstance().getAllUsers().snapshots.collect {
                    userToShowList.clear()
                    for (i in it.children) {
                        val user = i.getValue(User::class.java)!!
                        if (!userNotToShowList.contains(user) && !user.bannedUsers.contains(user.uid)) { // Если юзера нет в группе и тот кто добавляет не забанил нашего юзера
                            userToShowList.add(0, user)
                            adapter.notifyItemInserted(0)
                        }
                    }
                }
            }

        }


    }
}