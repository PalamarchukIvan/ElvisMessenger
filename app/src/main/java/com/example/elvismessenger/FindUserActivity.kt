package com.example.elvismessenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.databinding.ActivityFindUserBinding

class FindUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = binding.foundUserListFindUser
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Передаем в адптер созданый фейкером список
        recyclerView.adapter = ChatsListAdapter(ChatsListActivity.FakeChat.fakeItems)
    }
}