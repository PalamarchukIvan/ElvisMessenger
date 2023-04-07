package com.example.elvismessenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatLogAdapter(private val chatMessages: List<ChatLogActivity.ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER_MESSAGE_ME = 0
        private const val VIEW_TYPE_USER_MESSAGE_OTHER = 1
    }

    class MyUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.text_gchat_message_me)
        private val date: TextView = itemView.findViewById(R.id.text_gchat_date_me)
        private val time:  TextView = itemView.findViewById(R.id.text_gchat_timestamp_me)

        fun bind(messageItem: ChatLogActivity.ChatMessage) {
            text.text = messageItem.text
            time.text = messageItem.time
            date.text = "APR 10"
        }
    }

    class OtherUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pfp: ImageView = itemView.findViewById(R.id.image_gchat_profile_other)
        private val name: TextView = itemView.findViewById(R.id.text_gchat_user_other)
        private val text: TextView = itemView.findViewById(R.id.text_gchat_message_other)
        private val date: TextView = itemView.findViewById(R.id.text_gchat_date_other)
        private val time:  TextView = itemView.findViewById(R.id.text_gchat_timestamp_other)

        fun bind(messageItem: ChatLogActivity.OtherChatMessage) {
            pfp.setImageResource(R.drawable.dornan) // пока просто временное решение, подгрузка фотки из drawable
            name.text = messageItem.name
            text.text = messageItem.text
            time.text = messageItem.time
            date.text = "APR 10"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position] is ChatLogActivity.OtherChatMessage) {
            VIEW_TYPE_USER_MESSAGE_OTHER
        } else if (chatMessages[position] is ChatLogActivity.ChatMessage) {
            VIEW_TYPE_USER_MESSAGE_ME
        } else {
            -1
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> MyUserHolder(layoutInflater.inflate(R.layout.chat_me_item, parent, false))
            VIEW_TYPE_USER_MESSAGE_OTHER -> OtherUserHolder(layoutInflater.inflate(R.layout.chat_other_item, parent, false))
            else -> MyUserHolder(layoutInflater.inflate(R.layout.chat_me_item, parent, false)) //Generic return
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MyUserHolder
                holder.bind(chatMessages[position])
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherUserHolder
                holder.bind(chatMessages[position] as ChatLogActivity.OtherChatMessage)
            }
        }
    }

}