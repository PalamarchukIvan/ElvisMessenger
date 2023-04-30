package com.example.elvismessenger.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.elvismessenger.R
import com.example.elvismessenger.databinding.ActivityMainBinding
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.UserPersonalSettings
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navigationView: NavigationView

    private val userSettings = UserPersonalSettings.getInstance()

    companion object {
        lateinit var sp: SharedPreferences
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        sp = getSharedPreferences(SettingsFragment.SHARED_PREFERENCES, MODE_PRIVATE)

        //Настройка пользовательских настроек)
        userSettings.phoneNumber = sp.getString(SettingsFragment.PHONE_NUMBER, "").toString()
        userSettings.password = sp.getString(SettingsFragment.PASSWORD, "").toString()
        userSettings.email = sp.getString(SettingsFragment.EMAIL, "").toString()
        userSettings.ifDarkTheme = sp.getBoolean(SettingsFragment.THEME, false)
        userSettings.textSize = sp.getInt(SettingsFragment.TEXT_SIZE, 18)
        userSettings.language = sp.getString(SettingsFragment.LANGUAGE_SELECTED, "English").toString()
        userSettings.username = sp.getString(SettingsFragment.USERNAME, "").toString()
        userSettings.about = sp.getString(SettingsFragment.ABOUT, "").toString()
        userSettings.status = sp.getString(SettingsFragment.STATUS, "").toString()

        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigationView = binding.navView
        //Поиск главного фрагмента (чата)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        //доступ к дереву навигации
        navController = navHostFragment.findNavController()
        drawerLayout = binding.drawerLayout
        //связывание дерева навигации и шторки
        binding.navView.setupWithNavController(navController)

        //Хрен знает, что это и как работает, но по смыслу оно делает шторку кликабельной
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.getHeaderView(0).findViewById<TextView>(R.id.user_name_text_nav_header).text = userSettings.username
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.status_text_nav_header).text = userSettings.status
    }

    //выдвижение шторки, по сути кликабельность тоггла (кнопки-буттерброт)
    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.findNavController()

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Раздуваем макет верхних меню
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }
}