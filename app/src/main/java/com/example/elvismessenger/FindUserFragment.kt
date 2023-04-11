package com.example.elvismessenger

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FindUserFragment : Fragment(R.layout.fragment_find_user) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView: RecyclerView = view.findViewById(R.id.found_user_list_findUser)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ChatsListAdapter(ChatListFragment.FakeChat.fakeItems)

        super.onViewCreated(view, savedInstanceState)
    }
}