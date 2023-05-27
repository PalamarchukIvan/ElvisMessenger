package com.example.elvismessenger.db

import android.os.Parcelable
import com.example.elvismessenger.fragments.ChatListFragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    var id: String = "",
    var groupName: String? = "",
    var groupPhoto: String = "",
    var messages: HashMap<String, ChatMessage> = hashMapOf(),
    var whoAreWriting: MutableList<String> = mutableListOf(),
    var userList: List<String> = mutableListOf()) : Parcelable