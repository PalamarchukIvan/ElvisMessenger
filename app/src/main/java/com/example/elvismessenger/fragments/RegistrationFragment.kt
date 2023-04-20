package com.example.elvismessenger.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registrationButton: Button = view.findViewById(R.id.create_acc_button_reg)

        registrationButton.setOnClickListener {
            // не this а activity потому что делаем интент из фрагмента в активити
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

}