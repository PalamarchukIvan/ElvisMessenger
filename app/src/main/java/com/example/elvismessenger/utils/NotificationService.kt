package com.example.elvismessenger.utils

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val i = Intent(INTENT_FILTER)
        message.data.forEach {
            i.putExtra(it.key, it.value)
        }

        sendBroadcast(i)
    }

    companion object {
        const val INTENT_FILTER = "NEW_MESSAGE_EVENT"

        const val ACTION_KEY = "action"
        const val MESSAGE_KEY = "message"

        const val ACTION_NOTIFICATION = "show_notification"
    }
}