package com.example.elvismessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.javafaker.Faker

class ChatListFragment : Fragment() {

    data class ChatItem(val name: String, val status: String, val time: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = view.findViewById(R.id.list_recycler_view_chats_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Создаем адптер и передаем в него созданый фейкером список
        val chatAdapter = ChatsListAdapter(FakeChat.fakeItems)

        // Передаем адаптер
        recyclerView.adapter = chatAdapter

        chatAdapter.onItemClick = {
            val name = Bundle()
            name.putString("name", it.name)
            Navigation.findNavController(view).navigate(R.id.action_chatListFragment_to_chatLogFragment, name)
        }
    }

    object FakeChat {
        var fakeItems = mutableListOf<ChatItem>()

        init {
            val faker = Faker.instance()
            repeat(50) {
                fakeItems.add(
                    ChatItem(
                        name = faker.name().fullName(),
                        status = faker.rickAndMorty().character(),
                        time = "12:34"
                    )
                )
            }
        }
    }

}