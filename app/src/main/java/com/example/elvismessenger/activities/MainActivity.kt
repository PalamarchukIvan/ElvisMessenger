package com.example.elvismessenger.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.elvismessenger.R
import com.example.elvismessenger.databinding.ActivityMainBinding
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.NotificationService
import com.example.elvismessenger.utils.UserPersonalSettings
import com.firebase.ui.auth.data.model.User
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navigationView: NavigationView

    private val userSettings = UserPersonalSettings.livaDataInstance

    private var pushNotificationsBroadcastReceiver: BroadcastReceiver? = null

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

        sp = getSharedPreferences(SettingsFragment.SHARED_PREFERENCES, MODE_PRIVATE)

        drawerLayout = binding.drawerLayout
        //связывание дерева навигации и шторки
        binding.navView.setupWithNavController(navController)

        //Хрен знает, что это и как работает, но по смыслу оно делает шторку кликабельной
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView = binding.navView

        authoriseUser()

        //Устонавливаем слушатель на кнопку выхода
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.regLogActivity -> {
                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure that you want to logout?")
                        .setNegativeButton("no", null)
                        .setPositiveButton("yes") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this, RegLogActivity::class.java))
                            UserRepository.getInstance().makeNotActive()
                            finish()
                        }
                        .show()
                }
                R.id.settingsFragment -> {
                    navController.navigate(it.itemId)
                }
                else -> Toast.makeText(this, "In progress", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        UserRepository.currentUser?.observe(this) {
            val user = UserPersonalSettings.livaDataInstance.value
            user?.status = it.status
            user?.about = it.about
            user?.username = it.status
            user?.phoneNumber = it.about
            user?.password = it.status
            user?.email = it.about
            user?.photo = it.status
            userSettings.postValue(user)
            Log.d("User settings local ", userSettings.value.toString())
        }

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        UserRepository.currentUser?.observe(this) {
            if(networkInfo != null && networkInfo.isConnected) {
                UserRepository.updateSharedPreferances(it)
                navigationView.getHeaderView(0).findViewById<TextView>(R.id.user_name_text_nav_header).text = it.username
                navigationView.getHeaderView(0).findViewById<TextView>(R.id.status_text_nav_header).text = it.status
                navigationView.getHeaderView(0).findViewById<ImageView>(R.id.pfp_image_nav_header)
                    .apply {
                        if (it.photo.isNotBlank()) {
                            Picasso.get()
                                .load(it.photo.toUri())
                                .into(this)
                        } else {
                            Picasso.get()
                                .load(R.drawable.dornan)
                                .into(this)
                        }
                    }
            }
        }
        SettingsFragment.loadData()
        userSettings.observe(this) {
            if(networkInfo == null || !networkInfo.isConnected) {
                navigationView.getHeaderView(0).findViewById<TextView>(R.id.user_name_text_nav_header).text = it.username
                navigationView.getHeaderView(0).findViewById<TextView>(R.id.status_text_nav_header).text = it.status
                navigationView.getHeaderView(0).findViewById<ImageView>(R.id.pfp_image_nav_header)
                    .apply {
                        if (it.photo.isNotBlank()) {
                            Picasso.get()
                                .load(it.photo.toUri())
                                .placeholder(R.drawable.dornan)
                                .into(this)
                        } else {
                            Picasso.get()
                                .load(R.drawable.dornan)
                                .into(this)
                        }
                    }
            }
        }
    }

//    private fun setUpFirebaseMessaging() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener {
//            if(!it.isSuccessful)
//                return@addOnCompleteListener
//
//            val token = it.result
//            Log.d("Token: ", token!!)
//            val newUser = UserRepository.currentUser.value
//            newUser!!.cloudToken = token
//            UserRepository.currentUser.postValue(newUser)
//            UserRepository.getInstance().createOrUpdateUser(newUser)
//        }
//    }

    override fun onPause() {
        super.onPause()
        if(FirebaseAuth.getInstance().currentUser != null) {
            UserRepository.getInstance().makeNotActive()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(FirebaseAuth.getInstance().currentUser != null) {
            UserRepository.getInstance().makeNotActive()
        }
        pushNotificationsBroadcastReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if(FirebaseAuth.getInstance().currentUser != null) {
            UserRepository.getInstance().makeActive()
        }
    }

    private fun authoriseUser() {
        if(FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate(R.id.action_chatListFragment_to_regLogActivity)
            finish()
        } else {
            UserRepository.initCurrentUser()
            UserRepository.getInstance().makeActive()

//            setUpFirebaseMessaging()
            setUpBroadcastReceiver()
        }
    }

    private fun setUpBroadcastReceiver() {
        pushNotificationsBroadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent?.extras

                extras?.let {extras ->
                    extras.keySet().firstOrNull {it == NotificationService.ACTION_KEY }?.let {key ->
                        when(extras.getString(key)) {
                            NotificationService.ACTION_NOTIFICATION -> extras.getString(NotificationService.MESSAGE_KEY)?.let {message ->
                                Toast.makeText(
                                    this@MainActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
        //Это значит, что наш бродкаст ресивер тригерится на все акшены с ключем INTENT_FINTER
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationService.INTENT_FILTER)

        registerReceiver(pushNotificationsBroadcastReceiver, intentFilter)
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