package com.example.elvismessenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.elvismessenger.R
import com.squareup.picasso.Picasso

class OtherUserProfile : Fragment(R.layout.fragment_other_user_profile) {
    lateinit var name: String
    lateinit var about: String
    lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = arguments?.getString("otherUserName").toString()
        about = arguments?.getString("otherUserAbout").toString()
        status = arguments?.getString("otherUserStatus").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otherUserProfilePicture: ImageView = view.findViewById(R.id.other_user_pfp_image)
        val otherUserName: TextView = view.findViewById(R.id.other_username)
        val otherUserStatus: TextView = view.findViewById(R.id.other_user_status)
        val otherUserAbout: TextView = view.findViewById(R.id.other_user_about)

        otherUserName.text = name
        otherUserStatus.text = status
        otherUserAbout.text = about

        Picasso.get()
            .load(R.drawable.dornan)
            .fit()
            .centerCrop()
            .into(otherUserProfilePicture)
    }
}