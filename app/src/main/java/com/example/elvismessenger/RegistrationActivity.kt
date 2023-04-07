package com.example.elvismessenger

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elvismessenger.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegistrationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.regestrateButtonRegestration.setOnClickListener {
            val password1 = binding.password1PlainTextRegistration.text.toString()
            val password2 = binding.password2PlainTextRegistration.text.toString()
            if(password1 != password2) {
                Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userName = binding.userNamePlainTextRegistration.text.toString()
            val login = binding.loginPlainTextRegistration.text.toString()
            val i = Intent(this, ChatsListActivity::class.java)
            i.putExtra("username", "$userName")
            i.putExtra("login", "$login")
            i.putExtra("pwd", "$password1")
            startActivity(i)
        }
    }
}