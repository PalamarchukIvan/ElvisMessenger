package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.fragments.ChatLogFragment
import com.example.elvismessenger.R

class ChatLogAdapter(private val chatMessages: MutableList<ChatLogFragment.ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // Константы для обозначения типа сообщения (мое или чужое)
    companion object {
        private const val VIEW_TYPE_USER_MESSAGE_ME = 0
        private const val VIEW_TYPE_USER_MESSAGE_OTHER = 1
    }

    // Холдер для моих сообщений
    class MyUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.msg_text_chat_me)
        private val time:  TextView = itemView.findViewById(R.id.time_text_chat_me)

        fun bind(messageItem: ChatLogFragment.ChatMessage) {
            text.text = messageItem.text
            time.text = messageItem.time
        }
    }

    // Холдер для чужих сообщений
    class OtherUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_other)
        private val name: TextView = itemView.findViewById(R.id.name_text_chat_other)
        private val text: TextView = itemView.findViewById(R.id.msg_text_chat_other)
        private val time:  TextView = itemView.findViewById(R.id.time_text_chat_other)

        fun bind(messageItem: ChatLogFragment.OtherChatMessage) {
            pfp.setImageResource(R.drawable.dornan) // пока просто временное решение, подгрузка фотки из drawable
            name.text = messageItem.name
            text.text = messageItem.text
            time.text = messageItem.time
        }
    }

    fun addMessage(message: ChatLogFragment.ChatMessage) {
        chatMessages.add(0, message)
        notifyDataSetChanged() // обновляет рендер списка с новыми элементом (как я понял)
    }

    // Функция для нахождения какого типа должно быть отображаймое сообщение
    override fun getItemViewType(position: Int): Int {
        return when(chatMessages[position]) {
            is ChatLogFragment.OtherChatMessage -> VIEW_TYPE_USER_MESSAGE_OTHER
            is ChatLogFragment.ChatMessage -> VIEW_TYPE_USER_MESSAGE_ME
            else -> -1
        }
    }

    // Создаем viewHolder в зависимости от типа сообщения
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> MyUserHolder(layoutInflater.inflate(R.layout.chat_me_item, parent, false))
            VIEW_TYPE_USER_MESSAGE_OTHER -> OtherUserHolder(layoutInflater.inflate(R.layout.chat_other_item, parent, false))
            else -> MyUserHolder(layoutInflater.inflate(R.layout.chat_me_item, parent, false)) //Generic return
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    // Делаем биндинг в зависимости от типа холдера
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MyUserHolder
                holder.bind(chatMessages[position])
            }

            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherUserHolder
                holder.bind(chatMessages[position] as ChatLogFragment.OtherChatMessage)
            }
        }
    }
}