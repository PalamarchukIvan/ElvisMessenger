package com.example.elvismessenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChooseGroupAdapter
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
        const val USER_WHO_IS_ADDED = "user_who_is_added"
        const val IS_ADDING_USERS = "user_who_is_added"

        const val RESULT_LIST_CODE = "result_user_list_code"
        const val RESULT_LIST_DATA = "result_user_list"
    }

    private lateinit var group: Group
    private lateinit var user: User

    private lateinit var toChooseRecyclerView: RecyclerView
    private lateinit var confirmBrn: FloatingActionButton

    private lateinit var adapterChooseUser: ChooseUsersAdapter
    private val userNotToShowList = TreeSet<User>() // для быстроты поиска
    private val userToShowList = mutableListOf<User>()

    private lateinit var adapterChooseGroup: ChooseGroupAdapter
    private lateinit var userWhoIsAdded: User
    private val groupToShowList = mutableListOf<Group>()

    private val chosenStuff = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isChoosingUsers = false

        arguments?.let {
            it.getParcelable<Group>(GROUP_TO_ADD_MEMBERS)?.let { bundleGroup ->
                group = bundleGroup
            }
            it.getParcelable<User>(USER_WHO_ADD_MEMBERS)?.let {bundleUser ->
                user = bundleUser
            }
            it.getParcelable<User>(USER_WHO_IS_ADDED)?.let {bundleUser ->
                userWhoIsAdded = bundleUser
            }
            it.getBoolean(IS_ADDING_USERS)?.let {bundleBoolean ->
                isChoosingUsers = bundleBoolean
            }
        }

        if(isChoosingUsers) {
            initRecyclerViewForAddingUserToGroup(view)
        } else {
            initRecyclerViewForAddingGroupToUser(view)
        }
        confirmBrn = view.findViewById(R.id.confirm_group_adding)
        confirmBrn.setOnClickListener {
            setFragmentResult(RESULT_LIST_CODE, bundleOf(RESULT_LIST_DATA to chosenStuff))
            requireActivity().onBackPressed()
        }
    }

    private fun initRecyclerViewForAddingUserToGroup(view: View) {

        adapterChooseUser = ChooseUsersAdapter(userToShowList) {
            if(chosenStuff.contains(it.uid)) {
                chosenStuff.remove(it.uid)
                false
            }
            else {
                chosenStuff.add(it.uid)
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
                toChooseRecyclerView = view.findViewById(R.id.users_to_choose_RV)
                toChooseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                toChooseRecyclerView.adapter = adapterChooseUser
                UserRepository.getInstance().getAllUsers().snapshots.collect {
                    userToShowList.clear()
                    for (i in it.children) {
                        val user = i.getValue(User::class.java)!!
                        if (!userNotToShowList.contains(user) && !user.bannedUsers.contains(user.uid)) { // Если юзера нет в группе и тот кто добавляет не забанил нашего юзера
                            userToShowList.add(0, user)
                            adapterChooseUser.notifyItemInserted(0)
                        }
                    }
                }
            }

        }


    }

    private fun initRecyclerViewForAddingGroupToUser(view: View) {

        adapterChooseGroup = ChooseGroupAdapter(groupToShowList) {
            if(chosenStuff.contains(it.id)) {
                chosenStuff.remove(it.id)
                false
            }
            else {
                chosenStuff.add(it.id)
                true
            }
        }

        GroupRepository.getAllGroups().get().addOnSuccessListener {
            toChooseRecyclerView = view.findViewById(R.id.users_to_choose_RV)
            toChooseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            toChooseRecyclerView.adapter = adapterChooseGroup

            groupToShowList.clear()
            for (i in it.children) {
                val group = i.getValue(Group::class.java)!!

                if (!group.userList.contains(userWhoIsAdded.uid) && group.userList.contains(user.uid)) { // там есть мы и нет его
                    groupToShowList.add(0, group)
                    adapterChooseGroup.notifyItemInserted(0)
                }
            }
        }


    }
}