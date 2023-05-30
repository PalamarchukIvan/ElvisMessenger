package com.example.elvismessenger.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.SelectionManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GroupLogAdapter(
    private val options: FirebaseRecyclerOptions<ChatMessage>,
    private val currentGroup: Group,
    private val onItemClick: ((ChatMessage) -> Unit),
    private val onLongItemClick: (Int) -> Unit
) : FirebaseRecyclerAdapter<ChatMessage, GroupLogAdapter.GroupViewHolder>(options)  {

    private val messagesSelectedList = HashMap<Int, GroupViewHolder>()

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
        private val img: ImageView = itemView.findViewById(R.id.image_msg)

        fun bind(msg: ChatMessage) {
            val currentUser = FirebaseAuth.getInstance().currentUser!!

            if (msg.img.isNotEmpty()) {
                Picasso.get().load(msg.img).placeholder(R.drawable.baseline_image_24).into(img)
            } else {
                img.setImageDrawable(null)
            }

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
                    if(it.isNotEmpty()) {
                        Picasso
                            .get()
                            .load(it)
                            .placeholder(R.drawable.dornan)
                            .into(msgPhoto)
                    } else {
                        Picasso
                            .get()
                            .load(R.drawable.dornan)
                            .into(msgPhoto)
                    }
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
            if (position in messagesSelectedList.keys) {
                holder.chatMessage.setBackgroundColor(Color.parseColor(SelectionManager.UNSELECT_EVEN))
                messagesSelectedList.remove(position)
                if (messagesSelectedList.size == 0) {
                    onLongItemClick.invoke(View.INVISIBLE)
                }
            } else {
                holder.chatMessage.setBackgroundColor(Color.parseColor(SelectionManager.SELECT))
                messagesSelectedList[position] = holder
                onLongItemClick.invoke(View.VISIBLE)
            }
            true
        }
    }

    fun delete() {
        // TODO доделать удаление сообщений чтобы изменялось значение в лейтест месседжах
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val query = ChatRepository.getInstance()
            .getChat(ChatRepository.getChatID(currentUser.uid, currentGroup.id))

        if (messagesSelectedList.size == options.snapshots.size) {
            query.removeValue()
        } else {
            for (i in messagesSelectedList) {
                i.value.chatMessage.setBackgroundColor(Color.parseColor(SelectionManager.UNSELECT_EVEN))
                val msgId = options.snapshots.getSnapshot(i.key).key
                GroupRepository.deleteMessage(currentGroup.id, msgId.toString()) {
                    notifyDataSetChanged()
                }
            }
        }

        if (messagesSelectedList.containsKey(options.snapshots.count() - 1)) {
            for (uid in currentGroup.userList) {
                val latestMsgRef = FirebaseDatabase.getInstance().getReference("/users/$uid/latestMessages/${currentGroup.id}/text")
                latestMsgRef.setValue("[Deleted]")
            }
        }
        uncheckItems()
    }

    fun uncheckItems() {
        for (i in messagesSelectedList) {
            i.value.chatMessage.setBackgroundColor(Color.parseColor(SelectionManager.UNSELECT_EVEN))
        }
        messagesSelectedList.clear()
    }
}
