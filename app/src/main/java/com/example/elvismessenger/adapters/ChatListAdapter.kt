package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.ChatListFragment
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.database.ktx.snapshots
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ChatListAdapter(
    var chatList: MutableList<ChatListFragment.ChatItem>,
    private val onItemClick: ((User) -> Unit)
) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    companion object {
        private const val EVEN_CHAT = 0
        private const val ODD_CHAT = 1
    }

    class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        private val name: TextView = itemView.findViewById(R.id.name_text_chat_item)
        private val status: TextView = itemView.findViewById(R.id.status_text_chat_item)
        private val time: TextView = itemView.findViewById(R.id.time_text_chat_item)

        fun bind(chatItem: ChatListFragment.ChatItem) {
            if (chatItem.user?.photo != "") {
                Picasso.get().load(chatItem.user?.photo).into(pfp)
            } else {
                Picasso.get().load(R.drawable.dornan).into(pfp)
            }
            name.text = chatItem.user?.username
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

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(chatList[position])

        holder.itemView.setOnClickListener {
            onItemClick.invoke(chatList[position].user!!)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}