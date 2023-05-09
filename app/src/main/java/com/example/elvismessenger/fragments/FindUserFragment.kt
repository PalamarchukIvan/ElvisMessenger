package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.FindUserAdapter
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.launch

class FindUserFragment : Fragment(R.layout.fragment_find_user) {

    private val userRepository = UserRepository()

    private lateinit var adapter: FindUserAdapter

    private lateinit var userList: MutableList<User>//Сюда можно будет передать список друзей, типо сначала друзья появляют

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.found_user_list_find_user)

        userList = mutableListOf()

        //Заполнение списка юзеров из репозитория
        val query = userRepository.getAllUsers()

        lifecycleScope.launch {
            query.snapshots.collect {
                for(i in it.children) {
                    val user = i.getValue(User::class.java)
                    user.let {
                        if(user?.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                            userList.add(user!!)
                        }
                    }
                }
                adapter = FindUserAdapter(userList) {

                }
                recyclerView.adapter = adapter
            }
        }

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Для нахождения юзеров локально
        val inputText: EditText = view.findViewById(R.id.search_user_edit_text_find_user)

        inputText.addTextChangedListener {
            val newUserList = userList.filter { user ->
                user.username.contains(it.toString())
            }.toMutableList()
            adapter.userToShowList = newUserList
            adapter.notifyDataSetChanged()
        }
    }
}