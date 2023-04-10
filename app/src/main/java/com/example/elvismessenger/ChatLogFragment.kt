package com.example.elvismessenger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.javafaker.Faker

class ChatLogFragment : Fragment() {
    // Да это не дата классы и на это есть причина
    open class ChatMessage(val text: String, val time: String)
    class OtherChatMessage(val name: String, text: String, time: String) : ChatMessage(text, time)
    lateinit var recyclerView: RecyclerView
    lateinit var name: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_log, container, false)
        recyclerView = view.findViewById(R.id.list_recycler_view_chat_log)
        // Из прошлого активити получаем имя (временное решение шобы протестить)
        name = arguments?.getString("name").toString()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Часть кода для работы списка чатов
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