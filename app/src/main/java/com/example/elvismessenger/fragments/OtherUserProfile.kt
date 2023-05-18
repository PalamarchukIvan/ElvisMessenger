package com.example.elvismessenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.elvismessenger.R
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class OtherUserProfile : Fragment(R.layout.fragment_other_user_profile) {
    private lateinit var otherUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getParcelable<User>("otherUser")?.let { user ->
                otherUser = user
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otherUserProfilePicture: ImageView = view.findViewById(R.id.user_pfp_image)
        val otherUserName: TextView = view.findViewById(R.id.other_username)
        val otherUserState: TextView = view.findViewById(R.id.other_user_state)
        val otherUserStatus: TextView = view.findViewById(R.id.other_user_status)
        val otherUserAbout: TextView = view.findViewById(R.id.edit_about)

        otherUserName.text = otherUser.username
        otherUserStatus.text = otherUser.status
        otherUserAbout.text = otherUser.about

        UserRepository.getInstance().getUserByUID(otherUser.uid).addValueEventListener (object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                otherUserState.text = if (user!!.isActive) {
                    "Online"
                } else {
                    "Offline"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        if (otherUser.photo != "") {
            Picasso.get()
                .load(otherUser.photo)
                .fit()
                .centerCrop()
                .into(otherUserProfilePicture)
        } else {
            Picasso.get()
                .load(R.drawable.dornan)
                .fit()
                .centerCrop()
                .into(otherUserProfilePicture)
        }
    }
}