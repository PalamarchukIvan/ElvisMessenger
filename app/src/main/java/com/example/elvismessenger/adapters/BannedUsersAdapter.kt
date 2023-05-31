package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.fragments.ChatListFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class BannedUsersAdapter (
    private val options: FirebaseRecyclerOptions<User>,
    private val onLongItemClick: (Int) -> Unit
) : FirebaseRecyclerAdapter<User, BannedUsersAdapter.BannedUsersViewHolder>(options) {

    private val  unBanSelectedList = HashMap<User, BannedUsersViewHolder>()

    class BannedUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatName: TextView = itemView.findViewById(R.id.name_text_find_user_item)
        private val pfp: ImageView = itemView.findViewById(R.id.pfp_image_find_user_item)
        private val status: TextView = itemView.findViewById(R.id.status_text_find_user_item)
        val checkMark: ImageView = itemView.findViewById(R.id.check_mark)

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

        holder.itemView.setOnLongClickListener {
            unBanSelectedList[options.snapshots[position]] = holder
            holder.checkMark.visibility = View.VISIBLE
            onLongItemClick.invoke(View.VISIBLE)

            true
        }
    }

    fun uncheckItems() {
        for (i in unBanSelectedList) {
            i.value.checkMark.visibility = View.INVISIBLE
        }

        unBanSelectedList.clear()
    }

    fun delete() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val unBanQuery = FirebaseDatabase.getInstance().getReference("/users/${currentUser.uid}/bannedUsers")

        if (unBanSelectedList.size == options.snapshots.size) {
            unBanQuery.removeValue()
        } else {
            for (i in unBanSelectedList) {
                i.value.checkMark.visibility = View.INVISIBLE
                unBanQuery.child(i.key.uid).removeValue()
            }
        }

        uncheckItems()
    }
}