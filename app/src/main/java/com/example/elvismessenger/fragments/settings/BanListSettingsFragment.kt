package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.BannedUsersAdapter
import com.example.elvismessenger.adapters.ChatLogAdapter
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase

class BanListSettingsFragment : Fragment(R.layout.fragment_ban_list_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.banned_users_recycler_list)

        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val bannedUsersRef = FirebaseDatabase.getInstance().getReference("/users/${currentUser.uid}/bannedUsers")

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(bannedUsersRef, User::class.java)
            .setLifecycleOwner(this)
            .build()

        recyclerView.adapter = BannedUsersAdapter(options)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}