package com.example.elvismessenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsListAdapter(private val chatItems: List<ChatsListActivity.ChatItem>) : RecyclerView.Adapter<ChatsListAdapter.ChatViewHolder>() {
    // В переменную передаем из ChatsListActivity что наш клик на чат будет делать (лямбда)
    var onItemClick: ((ChatsListActivity.ChatItem) -> Unit)? = null

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        private val name: TextView = itemView.findViewById(R.id.name_text_chat_item)
        private val status: TextView = itemView.findViewById(R.id.status_text_chat_item)
        private val time: TextView = itemView.findViewById(R.id.time_text_chat_item)

        fun bind(chatItem: ChatsListActivity.ChatItem) {
            pfp.setImageResource(R.drawable.dornan) // пока просто временное решение, подгрузка фотки из drawable
            name.text = chatItem.name
            status.text = chatItem.status
            time.text = chatItem.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chats_item, parent, false)

        return ChatViewHolder(itemView)
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