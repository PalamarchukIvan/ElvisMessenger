package com.example.elvismessenger.db

import android.widget.Toast
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.fragments.ChatLogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatRepository private constructor(){
    fun newMessage(msg: ChatLogFragment.ChatMessage) {
        //Забил болт
    }

    fun getChat(child: String) = FirebaseDatabase.getInstance().getReference("chats").child(child)

    fun getOpenToUserChat() = FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().currentUser?.uid}/latestMessages/")

    fun sendMessage(msg: ChatLogFragment.ChatMessage, from: User, to: User, errorHandler: (DatabaseError?) -> Unit) {
        // Для записи этого же сообщения в список последних сообщений всех юзеров
        val chatItemMsg = ChatListFragment.ChatItem(name = to.username, pfp = to.photo, msg.text, System.currentTimeMillis(), to)
        val latestMsgRef = FirebaseDatabase.getInstance().getReference("/users/${from.uid}/latestMessages/${to.uid}")
        latestMsgRef.setValue(chatItemMsg)

        val chatItemMsgTo = ChatListFragment.ChatItem(name = from.username, pfp = from.photo, msg.text, System.currentTimeMillis(), from)
        val latestMsgToRef = FirebaseDatabase.getInstance().getReference("/users/${to.uid}/latestMessages/${from.uid}")
        latestMsgToRef.setValue(chatItemMsgTo)

        val chatQuery = getInstance().getChat(getChatID(from.uid, to.uid))

        chatQuery.ref.push().setValue(msg) { error, _ ->
            errorHandler.invoke(error)
        }
    }

    companion object {
        fun getInstance() = ChatRepository()

        fun getChatID(currentUserUID: String, otherUserUID: String) : String {
            return if(currentUserUID.compareTo(otherUserUID) > 1) {
                "${currentUserUID}_${otherUserUID}"
            } else {
                "${otherUserUID}_${currentUserUID}"
            }
        }
    }
}