package com.example.elvismessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.databinding.ChatsListActivityBinding
import com.github.javafaker.Faker
import com.google.android.material.navigation.NavigationView

class ChatsListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        actionBarDrawerToggle.onOptionsItemSelected(item)
        when(item.itemId) {
            R.id.nav_settings -> startActivity(Intent(this, FindUserActivity::class.java))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            //Проверяем на какую кнопку ты нажал
            when(item.itemId) {
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_add_new_friend -> startActivity(Intent(this, FindUserActivity::class.java))
            }

            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Раздуваем макет верхних меню
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
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