package com.example.elvismessenger.adapters

import android.graphics.Color
import com.example.elvismessenger.db.User
import com.example.elvismessenger.utils.SelectionManager

class CreateGroupAdapter(
    userToShowList: MutableList<User>,
    private val addUserToGroup: ((User) -> Boolean)
) : FindUserAdapter(userToShowList, null) {
    override fun onBindViewHolder(holder: FindUserViewHolder, position: Int) {
        holder.bind(userToShowList[position])

        // Тут обработка клика
        holder.itemView.setOnClickListener {
            val checkIfAlreadyWas =
                addUserToGroup.invoke(userToShowList[holder.absoluteAdapterPosition])
            holder.itemView.setBackgroundColor(
                Color.parseColor(
                    if (checkIfAlreadyWas) {
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