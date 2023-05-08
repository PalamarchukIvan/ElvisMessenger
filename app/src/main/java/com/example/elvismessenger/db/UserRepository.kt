package com.example.elvismessenger.db

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase

class UserRepository {

    fun createOrUpdateUser(user: User) {
        val database = Firebase.database

        val userDBRef = database.getReference("users")
        val userNodeREf = userDBRef.child(user.uid)
        userNodeREf.setValue(user)
    }

    fun getAllUsers() = FirebaseDatabase.getInstance().getReference("users")

    fun getUserByUID(uid: String) = FirebaseDatabase.getInstance().getReference("users").child(uid)

    fun getAllUsersWithNameContaining(partOfName: String) {

    }

    companion object {
        fun toUserDB(user: FirebaseUser, uPassword: String = "", username: String = "") =
            User(
                uid = user.uid,
                username = user.displayName ?: username,
                photo = user.photoUrl?.toString() ?: "",
                email = user.email ?: "no email",
                password = uPassword,
                phoneNumber = user.phoneNumber ?: "no phone number",
            )
    }
}