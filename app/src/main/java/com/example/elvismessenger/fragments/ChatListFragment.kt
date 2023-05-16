package com.example.elvismessenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatsListAdapter
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.javafaker.Faker
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {

    data class ChatItem(val name: String = "",
                        val pfp: String = "",
                        val text: String = "",
                        val time: Long = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.findUserFragment).isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = view.findViewById(R.id.list_recycler_view_chats_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        // Создаем адптер и передаем в него созданый фейкером список
        val currentUser = UserRepository.currentUser?.value
        val chatItemQuery = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}/latest-messages/")

        val options = FirebaseRecyclerOptions.Builder<ChatItem>()
            .setQuery(chatItemQuery, ChatItem::class.java)
            .setLifecycleOwner(this)
            .build()

        val chatAdapter = ChatsListAdapter(options)

        // Передаем адаптер
        recyclerView.adapter = chatAdapter

        chatAdapter.onItemClick = {
            val otherUserInfo = Bundle()
            otherUserInfo.putString("name", it.name)
            otherUserInfo.putString("about", it.text)
            Navigation.findNavController(view)
                .navigate(R.id.action_chatListFragment_to_chatLogFragment, otherUserInfo)
        }

    }

    private fun listenForLatestMessages() {
        val currentUser = UserRepository.currentUser?.value
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/${currentUser?.uid}")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogFragment.ChatMessage::class.java)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}