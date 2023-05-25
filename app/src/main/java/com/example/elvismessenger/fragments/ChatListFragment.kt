package com.example.elvismessenger.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatListAdapter
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.database.ktx.values
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    @Parcelize
    data class ChatItem(
        var text: String = "",
        val time: Long = 0,
        var isNew: Boolean = false,
        val user: User? = null) : Parcelable

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar
    private lateinit var deleteFAB: FloatingActionButton

    private var chatList: MutableList<ChatItem> = mutableListOf()
    private var lastMessagesCache: HashMap<String, String> = hashMapOf()

    private lateinit var navController: NavController

    private lateinit var findUserBtnMenu: MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        findUserBtnMenu = menu.findItem(R.id.find_user_item)
        findUserBtnMenu.isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // Часть кода для работы списка чатов
        recyclerView = view.findViewById(R.id.list_recycler_view_chats_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        deleteFAB = view.findViewById(R.id.delete_latest_chat_btn)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        chatListAdapter = ChatListAdapter(chatList, { chatItem, position ->
            chatItem.isNew = false
            ChatRepository.getInstance().getOpenToUserChat(UserRepository.currentUser.value!!.uid ,chatItem.user!!.uid).setValue(chatItem)
            chatListAdapter.notifyItemChanged(position)

            val anotherUser = chatItem.user
            val args = Bundle()
            args.putParcelable(ChatLogFragment.ANOTHER_USER, anotherUser)
            Navigation.findNavController(view).navigate(R.id.action_chatListFragment_to_chatLogFragment, args)
        },
            onLongItemClick = { state ->
                showDeleteFab(state)
            }
        )

        progressBar = view.findViewById(R.id.progress_bar_chat_list)

        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            recyclerView.adapter = chatListAdapter
            ChatRepository.getInstance().getOpenToUserChat(FirebaseAuth.getInstance().currentUser!!.uid)
                .snapshots.collect {
                    chatList.clear()
                    for (i in it.children) {
                        val chatItem  = i.getValue(ChatItem::class.java)!!
                        chatList.add(chatItem)
                        lastMessagesCache[chatItem.user?.uid]?.let { _ ->
                            lastMessagesCache[chatItem.user!!.uid] = chatItem.text
                        }
                        chatList.sortByDescending { chatItem ->
                            chatItem.time
                        }
                        chatListAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.INVISIBLE
                }
        }

        deleteFAB.setOnClickListener {
            chatListAdapter.delete()
            showDeleteFab(View.INVISIBLE)
            findUserBtnMenu.setIcon(R.drawable.ic_person_search)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.find_user_item -> {
                if (deleteFAB.visibility != View.VISIBLE) {
                    navController.navigate(R.id.action_chatListFragment_to_findUserFragment)
                } else {
                    chatListAdapter.uncheckItems()
                    showDeleteFab(View.INVISIBLE)
                    findUserBtnMenu.setIcon(R.drawable.ic_person_search)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteFab(state: Int) {
        findUserBtnMenu.setIcon(R.drawable.baseline_cancel_24)
        Toast.makeText(requireContext(), "worked", Toast.LENGTH_SHORT).show()
        deleteFAB.visibility = state
    }

    fun makeIsWritingState(uid1: String, uid2: String) {
        var i = 0
        chatList.forEach {
            if (it.user!!.uid == uid1) {
                lastMessagesCache[uid1] = it.text
                it.text = "Is writing..."
                chatListAdapter.notifyItemChanged(i)
                return
            } else if (it.user!!.uid == uid2) {
                lastMessagesCache[uid2] = it.text
                it.text = "Is writing..."
                chatListAdapter.notifyItemChanged(i)
                return
            }
            i++
        }
    }

    fun makeIsNotWritingState(uid1: String, uid2: String) {
        var i = 0
        chatList.forEach {
            if(it.user!!.uid == uid1) {
                it.text = lastMessagesCache[uid1].toString()
                lastMessagesCache[uid1] = ""
                chatListAdapter.notifyItemChanged(i)
                return
            } else if(it.user!!.uid == uid2) {
                it.text = lastMessagesCache[uid2].toString()
                lastMessagesCache[uid2] = ""
                chatListAdapter.notifyItemChanged(i)
                return
            }
            i++
        }
    }

}