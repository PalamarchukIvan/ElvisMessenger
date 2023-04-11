package com.example.elvismessenger

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.javafaker.Faker

class ChatLogFragment : Fragment(R.layout.fragment_chat_log) {
    // Да это не дата классы и на это есть причина
    open class ChatMessage(val text: String, val time: String)
    class OtherChatMessage(val name: String, text: String, time: String) : ChatMessage(text, time)

    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = arguments?.getString("name").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = view.findViewById(R.id.list_recycler_view_chat_log)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Создаем адптер и передаем в него созданый фейкером список
        val chatLogAdapter = ChatLogAdapter(FakeChat(name).fakeItems)

        // Передаем адаптер
        recyclerView.adapter = chatLogAdapter
    }

    class FakeChat(val name: String) {
        var fakeItems = mutableListOf<ChatMessage>()

        init {
            val faker = Faker.instance()
            repeat(5) {
                fakeItems.add(
                    ChatMessage(
                        text = faker.gameOfThrones().quote(),
                        time = "12:34"
                    )
                )
                fakeItems.add(
                    OtherChatMessage(
                        name = name,
                        text = faker.gameOfThrones().quote(),
                        time = "12:34"
                    )
                )
            }
        }
    }

}