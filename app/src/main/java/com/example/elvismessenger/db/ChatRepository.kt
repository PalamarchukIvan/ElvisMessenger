package com.example.elvismessenger.db

import com.example.elvismessenger.fragments.ChatLogFragment
import com.google.firebase.database.FirebaseDatabase

class ChatRepository private constructor(){
    fun newMessage(msg: ChatLogFragment.ChatMessage) {
        //Забил болт
    }

    fun getChat(child: String) = FirebaseDatabase.getInstance().getReference("chats").child(child).limitToLast(50)

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