package com.example.elvismessenger.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.javafaker.Bool
import com.squareup.picasso.Picasso
import kotlin.coroutines.coroutineContext

class FindUserAdapter(
    private val options: FirebaseRecyclerOptions<User>,
    private val toShowList: MutableList<User>,
    private val onItemClick: ((User) -> Unit)
) : FirebaseRecyclerAdapter<User, FindUserAdapter.FindUserViewHolder>(options) {

    class FindUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val chatName: TextView = itemView.findViewById(R.id.name_text_chat_item)
        private val photo: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        private val lastMsgDate: TextView = itemView.findViewById(R.id.time_text_chat_item)
        private val lastMsg: TextView = itemView.findViewById(R.id.status_text_chat_item)

        fun bind(user: User, isVisible: Boolean) {

            itemView.isGone = !isVisible

            Log.d("credit: ", "$user")
            chatName.text = user.username
            lastMsg.text = "last msg"
            lastMsgDate.text = "time"
            user.photo.let {
                if (it.isNotEmpty()) {
                    Picasso.get()
                        .load(it)
                        .into(photo)
                } else {
                    Picasso.get()
                        .load(R.drawable.dornan)
                        .into(photo)
                }
            }
        }
    }

    fun updateUserToShowList(newList: MutableList<User>) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindUserViewHolder {
        val holder = FindUserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chats_item, parent, false)
        )
        holder.itemView.setOnClickListener {
            onItemClick.invoke(options.snapshots[holder.absoluteAdapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: FindUserViewHolder, position: Int, model: User) {

        holder.bind(model, toShowList.contains(model))
    }

    override fun onDataChanged() {

    }
}