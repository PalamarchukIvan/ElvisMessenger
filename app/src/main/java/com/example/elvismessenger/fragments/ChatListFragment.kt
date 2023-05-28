package com.example.elvismessenger.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
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
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    @Parcelize
    data class ChatItem(
        var text: String = "",
        val time: Long = 0,
        var isNew: Boolean = false,
        var isGroup: Boolean = false,
        var id: String? = "",
        var photo: String? = "",
        var name: String? = "") : Parcelable

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
            val args = Bundle()

            chatItem.isNew = false
            ChatRepository.getInstance().getOpenToUserChat(
                UserRepository.currentUser.value!!.uid,
                chatItem.id!!
            ).setValue(chatItem)
            chatListAdapter.notifyItemChanged(position)

            if(chatItem.isGroup) {
                GroupRepository.getGroupById(chatItem.id!!).get()
                    .addOnSuccessListener {
                        val group = it.getValue(Group::class.java)
                        args.putParcelable(GroupLogFragment.ANOTHER_GROUP, group)
                        Navigation.findNavController(view).navigate(R.id.action_chatListFragment_to_groupLogFragment, args)
                }
            } else {
                UserRepository.getInstance().getUserByUID(chatItem.id!!).get()
                    .addOnSuccessListener {
                        val anotherUser = it.getValue(User::class.java)
                        args.putParcelable(ChatLogFragment.ANOTHER_USER, anotherUser)
                        Navigation.findNavController(view).navigate(R.id.action_chatListFragment_to_chatLogFragment, args)
                    }
            }
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
                        lastMessagesCache[chatItem.id!!] = chatItem.text

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
        deleteFAB.visibility = state
    }

    fun makeChatUserIsWritingState(uid1: String, uid2: String) {
        var i = 0
        chatList.forEach {
            if (it.id == uid1) {
                lastMessagesCache[uid1] = it.text
                it.text = "Is writing..."
                chatListAdapter.notifyItemChanged(i)
                return
            } else if (it.id == uid2) {
                lastMessagesCache[uid2] = it.text
                it.text = "Is writing..."
                chatListAdapter.notifyItemChanged(i)
                return
            }
            i++
        }
    }

    fun makeChatUserIsNotWritingState(uid1: String, uid2: String) {
        var i = 0
        chatList.forEach {
            if(it.id == uid1) {
                it.text = lastMessagesCache[uid1].toString()
                lastMessagesCache[uid1] = ""
                chatListAdapter.notifyItemChanged(i)
                return
            } else if(it.id == uid2) {
                it.text = lastMessagesCache[uid2].toString()
                lastMessagesCache[uid2] = ""
                chatListAdapter.notifyItemChanged(i)
                return
            }
            i++
        }
    }
}