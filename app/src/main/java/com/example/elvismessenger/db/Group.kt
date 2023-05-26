package com.example.elvismessenger.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    var groupName: String? = "",
    var messages: MutableList<ChatMessage>? = mutableListOf()) : Parcelable