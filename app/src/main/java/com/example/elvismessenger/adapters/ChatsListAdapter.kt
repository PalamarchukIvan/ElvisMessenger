package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.R

class ChatsListAdapter(private val chatItems: List<ChatListFragment.ChatItem>) : RecyclerView.Adapter<ChatsListAdapter.ChatViewHolder>() {
    // В переменную передаем из ChatsListActivity что наш клик на чат будет делать (лямбда)
    var onItemClick: ((ChatListFragment.ChatItem) -> Unit)? = null

    companion object {
        private const val EVEN_CHAT = 0
        private const val NOT_EVEN_CHAT = 1
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        private val name: TextView = itemView.findViewById(R.id.name_text_chat_item)
        private val status: TextView = itemView.findViewById(R.id.status_text_chat_item)
        private val time: TextView = itemView.findViewById(R.id.time_text_chat_item)

        fun bind(chatItem: ChatListFragment.ChatItem) {
            pfp.setImageResource(R.drawable.dornan) // пока просто временное решение, подгрузка фотки из drawable
            name.text = chatItem.name
            status.text = chatItem.status
            time.text = chatItem.time
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 2) {
            0 -> EVEN_CHAT
            else -> NOT_EVEN_CHAT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            EVEN_CHAT -> ChatViewHolder(layoutInflater.inflate(R.layout.chats_item_even, parent, false))
            NOT_EVEN_CHAT -> ChatViewHolder(layoutInflater.inflate(R.layout.chats_item, parent, false))
            else -> ChatViewHolder(layoutInflater.inflate(R.layout.chats_item_even, parent, false))
        }
    }

    override fun getItemCount() = chatItems.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatItems[position]

        holder.bind(chatItem)

        // Обрабатываем клик
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(chatItem)
        }
    }
}