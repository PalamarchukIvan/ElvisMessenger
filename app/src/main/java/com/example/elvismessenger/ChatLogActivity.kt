package com.example.elvismessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.elvismessenger.databinding.ActivityChatLogBinding

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Chat Log"
    }
}