package com.example.elvismessenger.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(val currentUserUID: String = "",
                       val otherUserUID: String = "",
                       val text: String = "",
                       val time: Long = 0) : Parcelable