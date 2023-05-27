package com.example.elvismessenger.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.utils.SelectionManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth

class GroupLogAdapter(
    private val options: FirebaseRecyclerOptions<ChatMessage>,
    private val currentGroup: Group,
    private val onItemClick: ((ChatMessage) -> Unit),
    private val onLongItemClick: (Int) -> Unit
) : FirebaseRecyclerAdapter<ChatMessage, GroupLogAdapter.GroupViewHolder>(options)  {


    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            const val ME = 1
            const val ANOTHER = 2
        }

        var chatMessage: ConstraintLayout = itemView.findViewById(R.id.chat_message)
        private var  message: TextView? = null
        private val  time: TextView = itemView.findViewById(R.id.time_message)

        fun bind(msg: ChatMessage) {
            val currentUser = FirebaseAuth.getInstance().currentUser!!
            if(currentUser.uid == msg.currentUserUID) {
                initForSender(msg)
            } else {
                initForReceiver(msg)
            }
            time.text = TimeAgo.using(msg.time)
        }

        private fun initForReceiver(msg: ChatMessage) {
            message = itemView.findViewById(R.id.message_text)
            message?.text = msg.text
        }

        private fun initForSender(msg: ChatMessage) {
            message = itemView.findViewById(R.id.message_text)
            message?.text = msg.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val holder = when(viewType) {
            ChatLogAdapter.ChatViewHolder.ME -> GroupViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
            )
            ChatLogAdapter.ChatViewHolder.ANOTHER -> GroupViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_me_item, parent, false)
            )

            else -> null
        }

        return holder!!
    }

    override fun getItemViewType(position: Int): Int {
        return GroupViewHolder.ME
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int, model: ChatMessage) {
        holder.bind(model)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(options.snapshots[position])
        }

        holder.itemView.setOnLongClickListener {
            holder.chatMessage.setBackgroundColor(Color.parseColor(SelectionManager.SELECT))
            onLongItemClick.invoke(View.VISIBLE)
            true
        }
    }
}
