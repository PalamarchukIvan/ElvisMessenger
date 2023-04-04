package com.example.elvismessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.databinding.ChatsListActivityBinding
import com.github.javafaker.Faker

class ChatsListActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ChatsListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = binding.listRecyclerViewChatsList
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Передаем в адптер созданый фейкером список
        recyclerView.adapter = ChatsListAdapter(FakeChat.fakeItems)

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
        var fakeItems = mutableListOf<ChatsListAdapter.ChatItem>()

        init {
            val faker = Faker.instance()
            repeat(50) {
                fakeItems.add(
                    ChatsListAdapter.ChatItem(
                        name = faker.name().fullName(),
                        status = faker.rickAndMorty().character(),
                        time = "12:34"
                    )
                )
            }
        }

    }
}