package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User
import com.example.elvismessenger.fragments.ChatLogFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ChatLogAdapter(
    private val options: FirebaseRecyclerOptions<ChatLogFragment.ChatMessage>,
    private val otherUser: User,
    private val onItemClick: ((ChatLogFragment.ChatMessage) -> Unit)
) : FirebaseRecyclerAdapter<ChatLogFragment.ChatMessage, ChatLogAdapter.ChatViewHolder>(options) {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            const val ME = 1
            const val ANOTHER = 2
        }

        private var  message: TextView? = null
        private val  time: TextView = itemView.findViewById(R.id.time_message)

        fun bind(msg: ChatLogFragment.ChatMessage) {

            val currentUser = FirebaseAuth.getInstance().currentUser!!
            if(currentUser.uid == msg.currentUserUID) {
                initForSender(msg)
            } else {
                initForReceiver(msg)
            }
            time.text = TimeAgo.using(msg.time)
        }

        private fun initForReceiver(msg: ChatLogFragment.ChatMessage) {
            message = itemView.findViewById(R.id.message_text)
            message?.text = msg.text
        }

        private fun initForSender(msg: ChatLogFragment.ChatMessage) {
            message = itemView.findViewById(R.id.message_text)
            message?.text = msg.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val holder = when(viewType) {
            ChatViewHolder.ME -> ChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
            )
            ChatViewHolder.ANOTHER -> ChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_me_item, parent, false)
            )

            else -> null
        }

        holder!!.itemView.setOnClickListener {
            onItemClick.invoke(options.snapshots[holder.absoluteAdapterPosition])
        }

        return holder
    }
    override fun getItemViewType(position: Int): Int {
        return when (options.snapshots[position].currentUserUID != FirebaseAuth.getInstance().currentUser!!.uid) {
            true -> ChatViewHolder.ME
            false -> ChatViewHolder.ANOTHER
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatLogFragment.ChatMessage) {
        holder.bind(model)
    }

}