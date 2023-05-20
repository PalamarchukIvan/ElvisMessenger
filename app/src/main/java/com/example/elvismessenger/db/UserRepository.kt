package com.example.elvismessenger.db

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class UserRepository private constructor() {


    fun createOrUpdateUser(user: User) {
        val database = Firebase.database

        val userDBRef = database.getReference("users")
        val userNodeREf = userDBRef.child(user.uid)

        userNodeREf.setValue(user)
    }

    fun addOrUpdateUserPhoto(photoUrl: Uri) {
        val storage = FirebaseStorage.getInstance()

        val userPhotoRef = storage.getReference("user_photos")
        val userPhotoNodeRef = userPhotoRef.child(currentUser?.value!!.uid)
        userPhotoNodeRef.putFile(photoUrl).addOnSuccessListener {
            userPhotoNodeRef.downloadUrl.addOnSuccessListener {
                val newUser = currentUser?.value
                newUser?.photo = it.toString()
                currentUser?.postValue(newUser!!)
                getInstance().createOrUpdateUser(newUser!!)
            }.addOnFailureListener {
                Log.d("Error with photo ", it.message.toString())
            }
        }.addOnFailureListener {
            Log.d("Error with photo ", it.message.toString())
        }
    }

    fun getAllUsers() = FirebaseDatabase.getInstance().getReference("users")

    fun getUserByUID(uid: String) = FirebaseDatabase.getInstance().getReference("users").child(uid)


    fun makeActive() {
        getInstance().getUserByUID(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { userDB ->
            val activeUser = userDB.getValue(User::class.java)!!
            activeUser.isActive = true
            getInstance().createOrUpdateUser(activeUser)
        }
    }

    fun makeNotActive() {
        FirebaseAuth.getInstance().currentUser?.let {
            getInstance().getUserByUID(it.uid).get()
                .addOnSuccessListener { userDB ->
                    val activeUser = userDB.getValue(User::class.java)!!
                    activeUser.isActive = false
                    activeUser.lastSeen = System.currentTimeMillis()
                    getInstance().createOrUpdateUser(activeUser)
                }
        }
    }

    companion object {
        var currentUser: MutableLiveData<User> = MutableLiveData()
            private set
        private var instance = UserRepository()
        fun initCurrentUser() {
            FirebaseAuth.getInstance().currentUser?.also {
                getInstance().getUserByUID(it.uid).get().addOnSuccessListener { userDB ->
                    currentUser.postValue(userDB.getValue(User::class.java))
                    setUpFirebaseMessaging()
                    getInstance().makeActive()
                }
            }
        }

        private fun setUpFirebaseMessaging() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if(!it.isSuccessful)
                    return@addOnCompleteListener

                val token = it.result
                Log.d("Token: ", token!!)
                val newUser = currentUser.value
                newUser!!.cloudToken = token
                currentUser.postValue(newUser!!)
                getInstance().createOrUpdateUser(newUser)
            }
        }

        fun getInstance() = instance

        fun toUserDB(user: FirebaseUser, uPassword: String = "", username: String = "", token: String = "") =
            User(
                uid = user.uid,
                username = user.displayName ?: username,
                photo = user.photoUrl?.toString() ?: "",
                email = user.email ?: "no email",
                password = uPassword,
                phoneNumber = user.phoneNumber ?: "no phone number",
                cloudToken = token,
            )

        fun updateSharedPreferences(user: User) {
            val editor = MainActivity.sp.edit()
            editor?.putString(SettingsFragment.EMAIL, user.email)
            editor?.putString(SettingsFragment.PASSWORD, user.password)
            editor?.putString(SettingsFragment.USERNAME, user.username)
            editor?.putString(SettingsFragment.ABOUT, user.about)
            editor?.putString(SettingsFragment.STATUS, user.status)
            editor?.putString(SettingsFragment.PHOTO, user.photo)
            editor?.putString(SettingsFragment.PHOTO, user.photo)
            editor?.apply()

            SettingsFragment.loadData()
        }

    }

}