package com.example.elvismessenger.db

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository {

    fun createOrUpdateUser(user: User) {
        val database = Firebase.database

        val userDBRef = database.getReference("users")
        val userNodeREf = userDBRef.child(user.uid)
        userNodeREf.push().setValue(user)
    }

    fun toUserDB(user: FirebaseUser, uPassword: String = "") =
        User(
            uid = user.uid,
            username = user.displayName ?: "no username",
            photo = user.photoUrl.toString(),
            password = uPassword,
            phoneNumber = user.phoneNumber ?: "no phonenumber",
        )
}