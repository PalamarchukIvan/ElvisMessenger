package com.example.elvismessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.databinding.ActivityChatsListBinding
import com.github.javafaker.Faker

class ChatsListActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    // Data class с переменными элемента списка (пока фото pfp не входит)
    data class ChatItem(val name: String, val status: String, val time: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = binding.listRecyclerViewChatsList
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Создаем адптер и передаем в него созданый фейкером список
        val chatAdapter = ChatsListAdapter(FakeChat.fakeItems)

        // Передаем адаптер
        recyclerView.adapter = chatAdapter

        // Для работы клика на чат из списка чатов
        chatAdapter.onItemClick = {
            val intent = Intent(this, ChatLogActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "${it.name} ${it.status} ${it.time}", Toast.LENGTH_SHORT).show() // Тут передаем интент на окно чата пока тост
        }

        // Часть кода для работы меню шторки
        drawerLayout = binding.menuDrawerLayoutChatsList
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,
            R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    // Просто временное решение чтобы заполнить списоок даннными из фейкера
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