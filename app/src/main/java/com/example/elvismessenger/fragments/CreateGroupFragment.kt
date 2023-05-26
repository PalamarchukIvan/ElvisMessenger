package com.example.elvismessenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.CreateGroupAdapter
import com.example.elvismessenger.adapters.FindUserAdapter
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.LinearLayoutManagerWrapper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.launch

class CreateGroupFragment : Fragment(R.layout.fragment_create_group) {

    private lateinit var confirmBtn: FloatingActionButton
    private lateinit var groupNameET: EditText
    private lateinit var groupImage: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: CreateGroupAdapter

    private var userList: MutableList<User> = mutableListOf()
    private val chosenUsers: MutableList<User> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmBtn = view.findViewById(R.id.confirm_group_creation)
        groupNameET = view.findViewById(R.id.group_name)
        groupImage = view.findViewById(R.id.selected_group_image)
        progressBar = view.findViewById(R.id.progress)


        val recyclerView = view.findViewById<RecyclerView>(R.id.user_list)
        recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext())

        adapter = CreateGroupAdapter(userList) {
            return@CreateGroupAdapter if(!chosenUsers.contains(it)) {
                chosenUsers.add(it)
                true
            } else {
                chosenUsers.remove(it)
                false
            }
        }

        initRecyclerViewContent(recyclerView)

    }

    private fun initRecyclerViewContent(recyclerView: RecyclerView) {
        if (userList.size == 0) {
            // Заполнение списка юзеров из репозитория
            val query = UserRepository.getInstance().getAllUsers()
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                query.snapshots.collect {
                    recyclerView.adapter = adapter
                    userList.clear()
                    for (i in it.children) {
                        val user = i.getValue(User::class.java)
                        user.let {
                            if (user?.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                                userList.add(user!!)
                                adapter.notifyItemChanged(0)
                            }
                        }
                    }
                    progressBar.visibility = View.INVISIBLE
                }
            }
        } else {
            recyclerView.adapter = adapter
        }
    }
}