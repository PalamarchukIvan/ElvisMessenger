package com.example.elvismessenger.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String= "",
    val password: String = "",
    val phoneNumber: String = "",
    val status: String = "",
    val about: String = "",
    val photo: String = ""):Parcelable