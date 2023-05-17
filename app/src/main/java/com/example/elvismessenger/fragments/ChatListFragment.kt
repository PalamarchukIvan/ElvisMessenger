package com.example.elvismessenger.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatListAdapter
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    @Parcelize
    data class ChatItem(val name: String = "",
                        val pfp: String = "",
                        val text: String = "",
                        val time: Long = 0,
                        val user: User? = null) : Parcelable

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var recyclerView: RecyclerView
    private val latestMessagesMap = HashMap<String, ChatItem>()
    private var chatList: MutableList<ChatItem> = mutableListOf()

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

        chatListAdapter = ChatListAdapter(chatList) { anotherUser ->
            val args = Bundle()
            args.putParcelable(ChatLogFragment.ANOTHER_USER, anotherUser)
            Navigation.findNavController(view).navigate(R.id.action_chatListFragment_to_chatLogFragment, args)
        }


        if(chatList.size == 0) {
            lifecycleScope.launch {
                recyclerView.adapter = chatListAdapter
                FirebaseDatabase.getInstance()
                    .getReference("/users/${FirebaseAuth.getInstance().currentUser?.uid}/latestMessages/")
                    .snapshots.collect {
                        if(chatList.size == 0) {
                            for (i in it.children) {
                                chatList.add(i.getValue(ChatItem::class.java)!!)
                                chatList.sortByDescending {chatItem ->
                                    chatItem.time
                                }
                                chatListAdapter.notifyDataSetChanged()
                            }
                        }
                    }
            }
        } else {
            recyclerView.adapter = chatListAdapter
        }

        listenForLatestMessages()

    }

    private fun refreshLatestMessagesMap() {
        val newChatList = mutableListOf<ChatItem>()

        latestMessagesMap.values.forEach {
            newChatList.add(it)
        }

        newChatList.sortByDescending { it.time }

        chatListAdapter.chatList = newChatList
        chatListAdapter.notifyDataSetChanged()
    }

    private fun listenForLatestMessages() {
        val currentUser = UserRepository.currentUser?.value
        val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}/latestMessages/")
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