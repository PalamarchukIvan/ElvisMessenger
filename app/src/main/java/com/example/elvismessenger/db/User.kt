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
    var bannedUsers: HashMap<String, User> = hashMapOf(),
    var cloudToken: String =  ""
) : Parcelable, Comparable<User> {
    override fun compareTo(other: User): Int {
        return hashCode().compareTo(other.hashCode())
    }
}

fun toBannedUser(user: User): User {
    user.email = ""
    user.password = ""
    user.phoneNumber = ""
    user.about = ""
    user.lastSeen = 0
    user.latestMessages.clear()
    user.bannedUsers.clear()
    user.cloudToken = ""
    return user
}