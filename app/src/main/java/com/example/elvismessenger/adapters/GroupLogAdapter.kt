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
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.SelectionManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

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
        private var msgPhoto: CircleImageView? = null
        private var msgUsername: TextView? = null

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
            msgPhoto = itemView.findViewById(R.id.group_other_user_photo)
            msgUsername = itemView.findViewById(R.id.group_other_username)
            UserRepository.getInstance().getUserByUID(msg.currentUserUID).get().addOnSuccessListener {userDb ->
                val sender: User = userDb.getValue(User::class.java)!!
                sender.photo.let {
                    Picasso
                        .get()
                        .load(it)
                        .into(msgPhoto)
                }
                msgUsername!!.text = sender.username
            }
        }

        private fun initForSender(msg: ChatMessage) {
            message = itemView.findViewById(R.id.message_text)
            message?.text = msg.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val holder = when(viewType) {
            GroupViewHolder.ANOTHER -> GroupViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_group_item, parent, false),
            )
            GroupViewHolder.ME -> GroupViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_me_item, parent, false),
            )

            else -> null
        }

        return holder!!
    }

    override fun getItemViewType(position: Int): Int {
        return  if(snapshots[position].currentUserUID == UserRepository.currentUser.value!!.uid) {
            GroupViewHolder.ME
        } else {
            GroupViewHolder.ANOTHER
        }
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
