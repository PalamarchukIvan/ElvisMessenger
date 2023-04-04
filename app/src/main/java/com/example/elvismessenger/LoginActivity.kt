package com.example.elvismessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elvismessenger.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButtonLogin.setOnClickListener {
            val login = binding.loginPlainTextLogin.text.toString()
            val password = binding.passwordPlainTextLogin.text.toString()
            //Тут будет переход к тебе
            val i = Intent(this, ChatsListActivity::class.java)
            i.putExtra("login", "$login")
            i.putExtra("password", "$password")
            startActivity(i)
        }

        binding.noAccauntTextLogin.setOnClickListener {
            val i = Intent(this, RegistrationActivity::class.java)
            startActivity(i)
        }
    }
}