package com.example.elvismessenger.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.elvismessenger.R
import com.example.elvismessenger.databinding.ActivityMainBinding
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.UserPersonalSettings
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navigationView: NavigationView

    private val userSettings = UserPersonalSettings.livaDataInstance

    companion object {
        lateinit var sp: SharedPreferences
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Поиск главного фрагмента (чата)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        //доступ к дереву навигации
        navController = navHostFragment.findNavController()
        authoriseUser()

        sp = getSharedPreferences(SettingsFragment.SHARED_PREFERENCES, MODE_PRIVATE)

        drawerLayout = binding.drawerLayout
        //связывание дерева навигации и шторки
        binding.navView.setupWithNavController(navController)

        //Хрен знает, что это и как работает, но по смыслу оно делает шторку кликабельной
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView = binding.navView

        SettingsFragment.loadData()

        //Первичная настрйока
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.user_name_text_nav_header).text = userSettings.value?.username ?: "no username"
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.status_text_nav_header).text = userSettings.value?.status ?: "no status"

        //Устонавливаем слушатель на кнопку выхода
        navigationView.setNavigationItemSelectedListener {
            if(it.itemId == R.id.regLogActivity) {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, RegLogActivity::class.java))
                finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        userSettings.observe(this) {
            navigationView.getHeaderView(0).findViewById<TextView>(R.id.user_name_text_nav_header).text = userSettings.value?.username
            navigationView.getHeaderView(0).findViewById<TextView>(R.id.status_text_nav_header).text = userSettings.value?.status
        }
    }

    private fun authoriseUser() {
        if(FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate(R.id.action_chatListFragment_to_regLogActivity)
            finish()
        }
    }

    //выдвижение шторки, по сути кликабельность тоггла (кнопки-буттерброт)
    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.findNavController()

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Раздуваем макет верхних меню
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }
}