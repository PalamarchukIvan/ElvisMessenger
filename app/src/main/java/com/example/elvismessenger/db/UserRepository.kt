package com.example.elvismessenger.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class UserRepository private constructor() {


    fun createOrUpdateUser(user: User) {
        val database = Firebase.database

        val userDBRef = database.getReference("users")
        val userNodeREf = userDBRef.child(user.uid)
        userNodeREf.setValue(user)
    }

    fun getAllUsers() = FirebaseDatabase.getInstance().getReference("users")

    fun getUserByUID(uid: String) = FirebaseDatabase.getInstance().getReference("users").child(uid)

    companion object {
        var currentUser: MutableLiveData<User>? = null
            get() {
                if(FirebaseAuth.getInstance().currentUser == null) {
                    return field
                }
                if (field == null || field?.value?.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                    field = MutableLiveData()
                    GlobalScope.launch(Dispatchers.IO) {
                        getInstance().getUserByUID(FirebaseAuth.getInstance().currentUser!!.uid).snapshots.collect {
                            field!!.postValue(it.getValue(User::class.java)!!)
                        }
                    }
                }
                Log.d("userCurrent ", field!!.value.toString())
                return field
            }

        fun getInstance() = UserRepository()

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