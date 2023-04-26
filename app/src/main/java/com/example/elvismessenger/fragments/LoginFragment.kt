package com.example.elvismessenger.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton: Button = view.findViewById(R.id.login_button_login)
        val noAccountTextLogin: TextView = view.findViewById(R.id.no_account_text_login)

        loginButton.setOnClickListener {
            // не this а activity потому что делаем интент из фрагмента в активити
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        noAccountTextLogin.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

}