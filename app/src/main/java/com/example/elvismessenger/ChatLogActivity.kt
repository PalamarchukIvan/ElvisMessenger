package com.example.elvismessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.databinding.ActivityChatLogBinding
import com.github.javafaker.Faker

class ChatLogActivity : AppCompatActivity() {
    // Да это не дата классы и на это есть причина
    open class ChatMessage(val text: String, val time: String)
    class OtherChatMessage(val name: String, text: String, time: String) : ChatMessage(text, time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Из прошлого активити получаем имя (временное решение шобы протестить)
        val name = intent.getStringExtra("name").toString()

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = binding.listRecyclerViewChatLog
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Создаем адптер и передаем в него созданый фейкером список
        val chatLogAdapter = ChatLogAdapter(FakeChat(name).fakeItems)

        // Передаем адаптер
        recyclerView.adapter = chatLogAdapter
    }

    // Класс а не object шобы протесть имя из прошлого активити + чтобы переписка в каждом чате была разной
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