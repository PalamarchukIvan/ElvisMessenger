package com.example.elvismessenger.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.fragments.ChatLogFragment
import com.example.elvismessenger.utils.SelectionManager.Companion.SELECT
import com.example.elvismessenger.utils.SelectionManager.Companion.UNSELECT_EVEN
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatLogAdapter(
    private val options: FirebaseRecyclerOptions<ChatMessage>,
    private val otherUser: User,
    private val onLongItemClick: (Int) -> Unit
) : FirebaseRecyclerAdapter<ChatMessage, ChatLogAdapter.ChatViewHolder>(options) {

    private val messagesSelectedList = HashMap<Int, ChatViewHolder>()

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            const val ME = 0
            const val ANOTHER = 1
        }

        var chatMessage: ConstraintLayout = itemView.findViewById(R.id.chat_message)
        private var  message: TextView? = null
        private val  time: TextView = itemView.findViewById(R.id.time_message)
        private val img: ImageView = itemView.findViewById(R.id.image_msg)

        fun bind(msg: ChatMessage) {
            val currentUser = FirebaseAuth.getInstance().currentUser!!

            if (msg.img.isNotEmpty()) {
                Picasso.get().load(msg.img).placeholder(R.drawable.baseline_image_24).into(img)
            } else {
                img.setImageDrawable(null)
            }

            if (currentUser.uid == msg.currentUserUID) {
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

        return holder!!
    }
    override fun getItemViewType(position: Int): Int {
        return when (options.snapshots[position].currentUserUID != FirebaseAuth.getInstance().currentUser!!.uid) {
            true -> ChatViewHolder.ME
            false -> ChatViewHolder.ANOTHER
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatMessage) {
        holder.bind(model)

        holder.itemView.setOnLongClickListener {
            if (position in messagesSelectedList.keys) {
                holder.chatMessage.setBackgroundColor(Color.parseColor(UNSELECT_EVEN))
                messagesSelectedList.remove(position)
                if (messagesSelectedList.size == 0) {
                    onLongItemClick.invoke(View.INVISIBLE)
                }
            } else {
                holder.chatMessage.setBackgroundColor(Color.parseColor(SELECT))
                messagesSelectedList[position] = holder
                onLongItemClick.invoke(View.VISIBLE)
            }

            true
        }
    }

    fun uncheckItems() {
        for (i in messagesSelectedList) {
            i.value.chatMessage.setBackgroundColor(Color.parseColor(UNSELECT_EVEN))
        }

        messagesSelectedList.clear()
    }

    fun delete() {
        // TODO доделать удаление сообщений чтобы изменялось значение в лейтест месседжах
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val query = ChatRepository.getInstance()
            .getChat(ChatRepository.getChatID(currentUser.uid, otherUser.uid))

        if (messagesSelectedList.size == options.snapshots.size) {
            query.removeValue()
        } else {
            for (i in messagesSelectedList) {
                i.value.chatMessage.setBackgroundColor(Color.parseColor(UNSELECT_EVEN))
                val msgId = options.snapshots.getSnapshot(i.key).key
                ChatRepository.getInstance().deleteMsg(ChatRepository.getChatID(currentUser.uid, otherUser.uid), msgId.toString()) {
                    notifyDataSetChanged()
                }
            }
        }

        if (messagesSelectedList.containsKey(options.snapshots.count() - 1)) {
            val latestMsgRef = FirebaseDatabase.getInstance().getReference("/users/${currentUser.uid}/latestMessages/${otherUser.uid}/text")
            latestMsgRef.setValue("[Deleted]")

            val latestMsgToRef = FirebaseDatabase.getInstance().getReference("/users/${otherUser.uid}/latestMessages/${currentUser.uid}/text")
            latestMsgToRef.setValue("[Deleted]")
        }

        uncheckItems()
    }
}