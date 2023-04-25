package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatsListAdapter
import java.time.LocalTime

class FindUserFragment : Fragment(R.layout.fragment_find_user) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.found_user_list_find_user)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        recyclerView.layoutManager = LinearLayoutManager(context)

        val users = ChatListFragment.FakeChat.fakeItems

        recyclerView.adapter = ChatsListAdapter(users)

        // Для нахождения юзеров локально
        val searchButton: Button = view.findViewById(R.id.find_button_find_user)
        val inputText: EditText = view.findViewById(R.id.search_user_edit_text_find_user)

        searchButton.setOnClickListener {
            recyclerView.adapter = ChatsListAdapter(users.filter {it.name.contains(inputText.text)})
        }
    }
}