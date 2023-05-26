package com.example.elvismessenger.db

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlin.streams.toList

object GroupRepository {
    fun createGroup(userList: MutableList<User>, groupName: String) {
        val groupReference = FirebaseDatabase.getInstance().getReference("groups").push()
        groupReference.setValue(
            userList.stream()
            .map {
                it.uid
            }.toList())
    }
}