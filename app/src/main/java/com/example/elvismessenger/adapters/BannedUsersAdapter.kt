package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso

class BannedUsersAdapter (
    options: FirebaseRecyclerOptions<User>,
) : FirebaseRecyclerAdapter<User, BannedUsersAdapter.BannedUsersViewHolder>(options) {

    class BannedUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatName: TextView = itemView.findViewById(R.id.name_text_find_user_item)
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_find_user_item)
        private val status: TextView = itemView.findViewById(R.id.status_text_find_user_item)

        fun bind(user: User) {
            chatName.text = user.username
            status.text = user.status
            user.photo.let {
                if (it.isNotEmpty()) {
                    Picasso.get()
                        .load(it)
                        .into(pfp)
                } else {
                    Picasso.get()
                        .load(R.drawable.no_pfp)
                        .into(pfp)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 2) {
            0 -> FindUserAdapter.EVEN_USER
            else -> FindUserAdapter.ODD_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannedUsersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            FindUserAdapter.EVEN_USER -> BannedUsersViewHolder(
                layoutInflater.inflate(
                    R.layout.find_user_item_even,
                    parent,
                    false
                )
            )
            FindUserAdapter.ODD_USER -> BannedUsersViewHolder(
                layoutInflater.inflate(
                    R.layout.find_user_item,
                    parent,
                    false
                )
            )
            else -> BannedUsersViewHolder(
                layoutInflater.inflate(
                    R.layout.find_user_item_even,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: BannedUsersViewHolder, position: Int, model: User) {
        holder.bind(model)
    }
}