package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.squareup.picasso.Picasso

class ChatsListAdapterFB(
    private val options: FirebaseRecyclerOptions<ChatListFragment.ChatItem>
) : FirebaseRecyclerAdapter<ChatListFragment.ChatItem, ChatsListAdapterFB.ChatListViewHolder>(options) {
    // В переменную передаем из ChatsListActivity что наш клик на чат будет делать (лямбда)
    var onItemClick: ((ChatListFragment.ChatItem) -> Unit)? = null

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
            if (chatItem.pfp != "") {
                Picasso.get().load(chatItem.pfp).into(pfp)
            } else {
                Picasso.get().load(R.drawable.dornan).into(pfp)
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
            EVEN_CHAT -> ChatListViewHolder(layoutInflater.inflate(R.layout.chats_item_even, parent, false))
            ODD_CHAT -> ChatListViewHolder(layoutInflater.inflate(R.layout.chats_item, parent, false))
            else -> ChatListViewHolder(layoutInflater.inflate(R.layout.chats_item_even, parent, false))
        }
    }

    override fun onBindViewHolder(
        holder: ChatListViewHolder,
        position: Int,
        model: ChatListFragment.ChatItem
    ) {
        holder.bind(model)
    }
}