package com.example.elvismessenger.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val currentUserUID: String = "",
    val text: String = "",
    val img: String= "",
    val time: Long = 0
) : Parcelable