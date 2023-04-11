package com.example.elvismessenger

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.Navigation

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registrationButton: Button = view.findViewById(R.id.regestrate_button_regestration)

        registrationButton.setOnClickListener {
            // не this а activity потому что делаем интент из фрагмента в активити
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

}