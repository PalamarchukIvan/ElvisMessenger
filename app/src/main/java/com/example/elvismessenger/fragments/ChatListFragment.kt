package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.adapters.ChatListAdapter
import com.example.elvismessenger.adapters.ChatsListAdapterFB
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    data class ChatItem(val name: String = "",
                        val pfp: String = "",
                        val text: String = "",
                        val time: Long = 0)

    private lateinit var recyclerView: RecyclerView
    private val latestMessagesMap = HashMap<String, ChatItem>()

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
        recyclerView= view.findViewById(R.id.list_recycler_view_chats_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        listenForLatestMessages()
    }

    private fun refreshLatestMessagesMap() {
        val chatList = mutableListOf<ChatItem>()

        latestMessagesMap.values.forEach {
            chatList.add(it)
        }

        chatList.sortByDescending { it.time }

        recyclerView.adapter = ChatListAdapter(chatList)
    }

    private fun listenForLatestMessages() {
        val currentUser = UserRepository.currentUser?.value
        val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}/latest-messages/")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                latestMessagesMap[snapshot.key!!] = snapshot.getValue<ChatItem>()!!
                refreshLatestMessagesMap()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                latestMessagesMap[snapshot.key!!] = snapshot.getValue<ChatItem>()!!
                refreshLatestMessagesMap()
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