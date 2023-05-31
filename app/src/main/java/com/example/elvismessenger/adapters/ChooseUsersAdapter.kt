package com.example.elvismessenger.adapters

import android.graphics.Color
import com.example.elvismessenger.db.User
import com.example.elvismessenger.utils.SelectionManager

class ChooseUsersAdapter(
    userToShowList: MutableList<User>,
    private val onItemClick: ((User) -> Boolean)
) : FindUserAdapter(userToShowList, null) {
    override fun onBindViewHolder(holder: FindUserViewHolder, position: Int) {
        holder.bind(userToShowList[position])

        // Тут обработка клика
        holder.itemView.setOnClickListener {
            holder.itemView.setBackgroundColor(
                Color.parseColor(
                    if (onItemClick.invoke(userToShowList[holder.absoluteAdapterPosition])) {
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
}