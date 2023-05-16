package com.example.elvismessenger.db

import android.os.Parcelable
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
    var lastSeen: Long = 0
) : Parcelable