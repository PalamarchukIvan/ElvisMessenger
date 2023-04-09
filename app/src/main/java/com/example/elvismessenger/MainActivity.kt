package com.example.elvismessenger

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.elvismessenger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Часть кода для работы меню шторки
        drawerLayout = binding.drawerLayout
        val navView = binding.navView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.setting_drawer -> {
                    val i = Intent(this, SettingsActivity::class.java)
                    startActivity(i)
                }
                R.id.contacts_drawer -> {
                    val i = Intent(this, ContactListActivity::class.java)
                    startActivity(i)
                }

                R.id.help_drawer -> Toast.makeText(this, "Тут будет сапорт, но пока нет смысла это делать", Toast.LENGTH_SHORT).show()
                R.id.new_account_drawer -> {
                    val i = Intent(this, RegistrationActivity::class.java)
                    startActivity(i)
                }
                R.id.nav_logout_drawer -> startActivity(Intent(this, LoginActivity::class.java))
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            //Проверяем на какую кнопку ты нажал
            when(item.itemId) {
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

}