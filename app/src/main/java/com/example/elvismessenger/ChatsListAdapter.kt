package com.example.elvismessenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsListAdapter(private val chatItems: List<ChatItem>) :
    RecyclerView.Adapter<ChatsListAdapter.ChatViewHolder>() {

    // Data class с переменными элемента списка (пока фото pfp не входит)
    data class ChatItem(val name: String, val status: String, val time: String)

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        var name: TextView = itemView.findViewById(R.id.name_text_chat_item)
        var status: TextView = itemView.findViewById(R.id.status_text_chat_item)
        var time: TextView = itemView.findViewById(R.id.time_text_chat_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chats_item, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun getItemCount() = chatItems.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.pfp.setImageResource(R.drawable.dornan) // пока просто временное решение, подгрузка фотки из drawable
        holder.name.text = chatItems[position].name
        holder.status.text = chatItems[position].status
        holder.time.text = chatItems[position].time
    }
}