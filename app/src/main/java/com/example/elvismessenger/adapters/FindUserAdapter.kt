package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User

import com.squareup.picasso.Picasso

open class FindUserAdapter(
    var userToShowList: MutableList<User>,
    private val onItemClick: ((User) -> Unit)?
) : RecyclerView.Adapter<FindUserAdapter.FindUserViewHolder>() {

    companion object {
        internal const val EVEN_USER = 0
        internal const val ODD_USER = 1
    }

    class FindUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
            0 -> EVEN_USER
            else -> ODD_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            EVEN_USER -> FindUserViewHolder(layoutInflater.inflate(R.layout.find_user_item_even, parent, false))
            ODD_USER -> FindUserViewHolder(layoutInflater.inflate(R.layout.find_user_item, parent, false))
            else -> FindUserViewHolder(layoutInflater.inflate(R.layout.find_user_item_even, parent, false))
        }
    }

    override fun onBindViewHolder(holder: FindUserViewHolder, position: Int) {
        holder.bind(userToShowList[position])

        // Тут обработка клика
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(userToShowList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount() = userToShowList.size
}