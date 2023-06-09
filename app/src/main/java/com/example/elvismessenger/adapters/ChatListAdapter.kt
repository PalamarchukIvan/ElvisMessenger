package com.example.elvismessenger.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.ChatListFragment
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ChatListAdapter(
    private var chatList: MutableList<ChatListFragment.ChatItem>,
    private val onItemClick: ((ChatListFragment.ChatItem, Int) -> Unit),
    private val onLongItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    private val chatsSelectedList = HashMap<ChatListFragment.ChatItem, ChatListViewHolder>()

    companion object {
        private const val EVEN_CHAT = 0
        private const val ODD_CHAT = 1
    }

    class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        private val name: TextView = itemView.findViewById(R.id.name_text_chat_item)
        private val status: TextView = itemView.findViewById(R.id.status_text_chat_item)
        private val time: TextView = itemView.findViewById(R.id.time_text_chat_item)
        private val newMessageMark: ImageView = itemView.findViewById(R.id.new_message_notification)
        val checkMark: ImageView = itemView.findViewById(R.id.check_mark)

        fun bind(chatItem: ChatListFragment.ChatItem) {
            if(chatItem.isNew) {
                newMessageMark.isVisible = true
            }
            if (chatItem.photo != null && chatItem.photo != "") {
                Picasso.get().load(chatItem.photo).into(pfp)
            } else {
                Picasso.get().load(R.drawable.no_pfp).into(pfp)
            }
            name.text = chatItem.name
            status.text = chatItem.text
            time.text = TimeAgo.using(chatItem.time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 2) {
            0 -> EVEN_CHAT
            else -> ODD_CHAT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            EVEN_CHAT -> ChatListViewHolder(
                layoutInflater.inflate(
                    R.layout.chats_item_even,
                    parent,
                    false
                )
            )
            ODD_CHAT -> ChatListViewHolder(
                layoutInflater.inflate(
                    R.layout.chats_item,
                    parent,
                    false
                )
            )
            else -> ChatListViewHolder(
                layoutInflater.inflate(
                    R.layout.chats_item_even,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(chatList[position])

        holder.itemView.setOnClickListener {
            onItemClick.invoke(chatList[position], position)
        }

        holder.itemView.setOnLongClickListener {
            holder.checkMark.visibility = View.VISIBLE
            chatsSelectedList[chatList[position]] = holder
            onLongItemClick.invoke(View.VISIBLE)
            true
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun uncheckItems() {
        for (i in chatsSelectedList) {
            i.value.checkMark.visibility = View.INVISIBLE
        }

        chatsSelectedList.clear()
    }

    fun delete() {
        if (chatsSelectedList.size == chatList.size) {
            val query = FirebaseDatabase
                .getInstance()
                .getReference("/users/${UserRepository.currentUser.value!!.uid}/latestMessages")
            query.removeValue()
            chatList.clear()
        } else {
            for (i in chatsSelectedList) {
                val query = FirebaseDatabase
                    .getInstance()
                    .getReference("/users/${UserRepository.currentUser.value!!.uid}/latestMessages/${i.key.id ?: i.key.id}")
                query.removeValue()
                chatList.remove(i.key)
            }
        }

        uncheckItems()
        notifyDataSetChanged()
    }

}