package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatsListAdapter

class FindUserFragment : Fragment(R.layout.fragment_find_user) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.found_user_list_findUser)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ChatsListAdapter(ChatListFragment.FakeChat.fakeItems)
    }
}