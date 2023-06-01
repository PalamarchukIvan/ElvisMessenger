package com.example.elvismessenger.utils

import android.content.Intent
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.github.javafaker.Bool
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val i = Intent(INTENT_FILTER)
        message.data.forEach {
            i.putExtra(it.key, it.value)
        }

        i.putExtra(BODY_KEY, message.notification?.body)
        i.putExtra(TITLE_KEY, message.notification?.title)

        sendBroadcast(i)
    }

    companion object {
        const val INTENT_FILTER = "NEW_MESSAGE_EVENT"

        const val ACTION_KEY = "action"
        const val MESSAGE_KEY = "message"
        const val BODY_KEY = "body"
        const val TITLE_KEY = "title"

        const val ACTION_NOTIFICATION = "show_notification"

        const val ACTION_IS_WRITING = "show_is_writing"
        const val ACTION_IS_NOT_WRITING = "show_is_not_writing"
        fun ifToShowNotification(from: String, to: String): Boolean {
            return FirebaseAuth.getInstance().currentUser?.uid == from || FirebaseAuth.getInstance().currentUser?.uid == to
        }
    }
}