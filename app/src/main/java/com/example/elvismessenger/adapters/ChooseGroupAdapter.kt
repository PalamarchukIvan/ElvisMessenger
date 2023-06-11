package com.example.elvismessenger.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.FindUserAdapter.Companion.EVEN_USER
import com.example.elvismessenger.adapters.FindUserAdapter.Companion.ODD_USER
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.User
import com.example.elvismessenger.utils.SelectionManager
import com.squareup.picasso.Picasso
import java.lang.RuntimeException

class ChooseGroupAdapter(
    private val groupToShowList: MutableList<Group>,
    private val onItemClick: ((Group) -> Boolean)
) : RecyclerView.Adapter<ChooseGroupAdapter.ChooseGroupViewHolder>() {


    class ChooseGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chatName: TextView = itemView.findViewById(R.id.name_text_chat_item)
        val pfp: ImageView = itemView.findViewById(R.id.pfp_image_chat_item)
        val status: TextView = itemView.findViewById(R.id.status_text_chat_item)

        fun bind(group: Group) {
            chatName.text = group.groupName
            status.text = "${group.userList.size} Members"
            group.groupPhoto.let {
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

    override fun onBindViewHolder(holder: ChooseGroupViewHolder, position: Int) {
        holder.chatName.text = groupToShowList[position].groupName
        holder.pfp.let {
            if(groupToShowList[position].groupPhoto.isNotEmpty()) {
                Picasso.get().load(groupToShowList[position].groupPhoto).into(it)
            } else {
                Picasso.get().load(R.drawable.no_pfp).into(it)
            }
        }

        // Тут обработка клика
        holder.itemView.setOnClickListener {
            holder.itemView.setBackgroundColor(
                Color.parseColor(
                    if (onItemClick.invoke(groupToShowList[holder.absoluteAdapterPosition])) {
                        SelectionManager.SELECT
                    } else {
                        if (holder.absoluteAdapterPosition % 2 == 0) {
                            SelectionManager.UNSELECT_EVEN
                        } else {
                            SelectionManager.UNSELECT_ODD
                        }
                    }
                )
            )

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 2) {
            0 -> EVEN_USER
            else -> ODD_USER
        }
    }

    override fun getItemCount(): Int = groupToShowList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseGroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            EVEN_USER -> ChooseGroupViewHolder(layoutInflater.inflate(R.layout.chats_item_even, parent, false))
            ODD_USER -> ChooseGroupViewHolder(layoutInflater.inflate(R.layout.chats_item, parent, false))
            else -> throw RuntimeException("Wrong viewType")
        }
    }


}