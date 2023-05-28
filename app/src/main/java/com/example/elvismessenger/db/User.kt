package com.example.elvismessenger.db

import android.os.Parcelable
import com.example.elvismessenger.fragments.ChatListFragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = "",
    var status: String = "",
    var about: String = "",
    var photo: String = "",
    var isActive: Boolean = false,
    var lastSeen: Long = 0,
    var latestMessages: HashMap<String, ChatListFragment.ChatItem> = hashMapOf(),
    var bannedUsers: HashMap<String, String> = hashMapOf(),
    var cloudToken: String =  ""
) : Parcelable

fun userToLatestMsgUser(user: User): User {
    user.latestMessages.clear()
    return user
}