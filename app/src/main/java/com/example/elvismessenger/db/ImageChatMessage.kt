package com.example.elvismessenger.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageChatMessage(val currentUserUID: String = "",
                            val otherUserUID: String = "",
                            val image: String = "",
                            val time: Long = 0)  : Parcelable