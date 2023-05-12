package com.example.elvismessenger.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.elvismessenger.R
import com.example.elvismessenger.databinding.ActivityRegLogBinding
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment

class RegLogActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    companion object {
        const val GOOD = 1

        const val INCORRECT_USERNAME = -1
        const val INCORRECT_EMAIL = -2
        const val INCORRECT_PASSWORD = -3
        const val PASSWORDS_DO_NOT_MATCH = -4

        const val INCORRECT_LOGIN_CREDITS = -5

        const val INCORRECT_USERNAME_SIZE = -6
        const val INCORRECT_STATUS_SIZE = -7
        const val INCORRECT_ABOUT_SIZE = -8

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binging = ActivityRegLogBinding.inflate(layoutInflater)
        setContentView(binging.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()
    }
}