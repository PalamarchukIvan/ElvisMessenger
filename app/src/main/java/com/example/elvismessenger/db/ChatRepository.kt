package com.example.elvismessenger.db

import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.fragments.ChatLogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class ChatRepository private constructor(){
    fun newMessage(msg: ChatLogFragment.ChatMessage) {
        //Забил болт
    }

    fun getChat(child: String) = FirebaseDatabase.getInstance().getReference("chats").child(child)

    fun getOpenToUserChat() = FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().currentUser?.uid}/latestMessages/")

//    fun sendMessage(msg: ChatLogFragment.ChatMessage, currentUser: User, otherUser: User, chatQuery: Query, errorHandler: (DatabaseError?) -> Unit) {
//        // Для записи этого же сообщения в список последних сообщений всех юзеров
//        val chatItemMsg = ChatListFragment.ChatItem(msg.text, System.currentTimeMillis(), otherUser)
//        val latestMsgRef = FirebaseDatabase.getInstance().getReference("/users/${currentUser.uid}/latestMessages/${otherUser.uid}")
//        latestMsgRef.setValue(chatItemMsg)
//
//        val chatItemMsgTo = ChatListFragment.ChatItem(msg.text, System.currentTimeMillis(), currentUser)
//        val latestMsgToRef = FirebaseDatabase.getInstance().getReference("/users/${otherUser.uid}/latestMessages/${currentUser.uid}")
//        latestMsgToRef.setValue(chatItemMsgTo)
//
//        chatQuery.ref.push().setValue(msg) { error, _ ->
//            errorHandler.invoke(error)
//        }
//    }


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