package com.example.elvismessenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatsListAdapter
import com.github.javafaker.Faker

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {

    data class ChatItem(val name: String, val status: String, val time: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.findUserFragment).isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = view.findViewById(R.id.list_recycler_view_chats_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        // Создаем адптер и передаем в него созданый фейкером список
        val chatAdapter = ChatsListAdapter(FakeChat.fakeItems)

        // Передаем адаптер
        recyclerView.adapter = chatAdapter

        chatAdapter.onItemClick = {
            val name = Bundle()
            name.putString("name", it.name)
            Navigation.findNavController(view).navigate(R.id.action_chatListFragment_to_chatLogFragment, name)
        }

        loadSettingsData()
    }

    private fun loadSettingsData() {
//        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
//
//        val name = sharedPreference.getString("name", "")
//        Log.d("settings test", "1")
//        val about = sharedPreference.getString("about", "")
//        Log.d("settings test", "2")
//
//        val email = sharedPreference.getString("email", "")
//        Log.d("settings test", "3")
//        val password = sharedPreference.getString("password", "")
//        Log.d("settings test", "4")
//        val ifCanSpy = sharedPreference.getBoolean("spying", false);
//        Log.d("settings test", "5")
//
//        val language = sharedPreference.getString("language", "english")
//        Log.d("settings test", "6")
//        val volume = sharedPreference.getInt("volume", 40)
//        Log.d("settings test", "7")
//        val textSize = sharedPreference.getString("text_size", "")
//        Log.d("settings test", "8")
//        val ifDarkTheme = sharedPreference.getBoolean("theme", false)
//        Log.d("settings test", "9")
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