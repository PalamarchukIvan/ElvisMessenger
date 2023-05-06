package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatsListAdapter
import com.example.elvismessenger.adapters.FindUserAdapter
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.time.LocalTime

class FindUserFragment : Fragment(R.layout.fragment_find_user) {

    private val options = FirebaseRecyclerOptions.Builder<User>()
        .setLifecycleOwner(this)
        .setQuery(UserRepository().getUsers(), User::class.java)
        .build()

    private val adapter = FindUserAdapter(options) {
        //делаем переход в чат
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.found_user_list_find_user)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Для нахождения юзеров локально
        val inputText: EditText = view.findViewById(R.id.search_user_edit_text_find_user)

        inputText.addTextChangedListener {
            adapter.onFindByPartOfName(it?.toString() ?: "")
        }
    }
}