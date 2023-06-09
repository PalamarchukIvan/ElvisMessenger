package com.example.elvismessenger.db

import android.content.Context
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.utils.FCMSender
import com.example.elvismessenger.utils.NotificationService
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRepository private constructor(){
    fun getChat(child: String) = FirebaseDatabase.getInstance().getReference("chats").child(child)

    fun getOpenToUserChat(userUID: String) = FirebaseDatabase.getInstance().getReference("/users/${userUID}/latestMessages/")
    fun getOpenToUserChat(userUID: String, otherUserUID: String) = Firebase.database
        .getReference("users")
        .child(userUID)
        .child("latestMessages")
        .child(otherUserUID)

    fun sendMessage(msg: ChatMessage, currentUser: User, otherUser: User, chatQuery: Query, context: Context, errorHandler: (DatabaseError?) -> Unit) {
        // Для записи этого же сообщения в список последних сообщений всех юзеров
        val chatItemMsg = ChatListFragment.ChatItem(msg.text, System.currentTimeMillis(), false, id = otherUser.uid, name = otherUser.username, photo = otherUser.photo)
        val latestMsgRef = getOpenToUserChat(currentUser.uid, otherUser.uid)
        if (msg.img.isNotEmpty()) chatItemMsg.text = "Photo"

        latestMsgRef.setValue(chatItemMsg)

        val chatItemMsgTo = ChatListFragment.ChatItem(msg.text, System.currentTimeMillis(), true, id = currentUser.uid, name = currentUser.username, photo = currentUser.photo)
        val latestMsgToRef = getOpenToUserChat(otherUser.uid, currentUser.uid)
        if (msg.img.isNotEmpty()) chatItemMsgTo.text = "Photo"

        latestMsgToRef.setValue(chatItemMsgTo)

        chatQuery.ref.push().setValue(msg) { error, _ ->
            error?.let {
                errorHandler.invoke(it)
            }
        }

        FCMSender.pushNotification(context, otherUser.cloudToken, currentUser.username, msg.text, currentUser.uid, otherUser.uid, NotificationService.ACTION_NOTIFICATION)
    }

    fun deleteMsg(chatId: String, msgId: String, onSuccess: () -> Unit) {
        val query = getChat(chatId).child(msgId)
        query.removeValue().addOnSuccessListener {
            onSuccess.invoke()
        }
    }

    companion object {
        private val instance = ChatRepository()
        fun getInstance() = instance

        fun getChatID(currentUserUID: String, otherUserUID: String) : String {
            return if(currentUserUID > otherUserUID) {
                "${currentUserUID}_${otherUserUID}"
            } else {
                "${otherUserUID}_${currentUserUID}"
            }
        }
    }
}