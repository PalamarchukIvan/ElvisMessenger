package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GroupProfileAdapter(
    options: FirebaseRecyclerOptions<String>,
    private val onUserClicked: (User) -> Unit
    ) :
    FirebaseRecyclerAdapter<String, GroupProfileAdapter.MemberViewHolder>(options) {

    companion object {
        private const val EVEN = 0
        private const val ODD = 1
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userPhoto: CircleImageView = itemView.findViewById(R.id.pfp_image_find_user_item)
        private val username: TextView = itemView.findViewById(R.id.name_text_find_user_item)
        private val userStatus: TextView = itemView.findViewById(R.id.status_text_find_user_item)
        fun bind(user: User) {
            userStatus.text = user.status
            username.text = user.username
            user.photo.let {
                if(it.isNotEmpty()) {
                    Picasso
                        .get()
                        .load(it)
                        .into(userPhoto)
                } else {
                    Picasso
                        .get()
                        .load(R.drawable.no_pfp)
                        .into(userPhoto)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position % 2 == 0) {
            return EVEN
        } else {
            return ODD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return if(viewType == ODD) {
            MemberViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.find_user_item, parent, false),
            )
        } else {
            MemberViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.find_user_item_even, parent, false),
            )
        }
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int, model: String) {
        UserRepository.getInstance().getUserByUID(model).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)!!
            holder.bind(user)

            holder.itemView.setOnClickListener {
                onUserClicked.invoke(user)
            }
        }
    }

}