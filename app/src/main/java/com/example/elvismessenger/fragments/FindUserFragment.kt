package com.example.elvismessenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.FindUserAdapter
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FindUserFragment : Fragment(R.layout.fragment_find_user) {

    private val userRepository = UserRepository()

    private lateinit var options: FirebaseRecyclerOptions<User>

    private lateinit var adapter: FindUserAdapter

    private val userList: MutableList<User> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.found_user_list_find_user)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val query = userRepository.getAllUsers()
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(User::class.java)?.let { userList.add(it) }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(User::class.java)?.let { snapshotUser ->
                    userList.remove(userList.find { listUser ->
                        listUser.uid == previousChildName
                    })
                    userList.add(snapshotUser)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue(User::class.java)?.let {
                    userList.remove(it)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Тут ниче не надо
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error with loading userList", Toast.LENGTH_SHORT).show()
            }

        })
        options = FirebaseRecyclerOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(query, User::class.java)
            .build()

        adapter = FindUserAdapter(options, userList) {
            //делаем переход в чат
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Для нахождения юзеров локально
        val inputText: EditText = view.findViewById(R.id.search_user_edit_text_find_user)

        inputText.addTextChangedListener {
            if(!it.isNullOrBlank()) {
                val newUserList = userList.filter { user ->
                    user.username.contains(it.toString())
                }.toMutableList()
                recyclerView.adapter = FindUserAdapter(options, newUserList) {

                }
            } else {
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}