package com.example.elvismessenger.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.activities.RegLogActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var logEmail: EditText
    private lateinit var logPassword: EditText
    private lateinit var logo: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logo = view.findViewById(R.id.login_logo_img)
        val loginButton: Button = view.findViewById(R.id.login_button_login)
        val noAccountTextLogin: TextView = view.findViewById(R.id.no_account_text_login)

        logEmail = view.findViewById(R.id.email_text_login)
        logPassword = view.findViewById(R.id.password_text_login)

        setUpLayout()

        loginButton.setOnClickListener {

            val email = logEmail.text.toString()
            val password = logPassword.text.toString()

            val validation = validateLogData(email, password)

            when(validation) {

                RegLogActivity.GOOD -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_loginFragment_to_mainActivity)
                                activity?.finish()
                            } else {
                                Toast.makeText(
                                    context,
                                    "user with such credits does not exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                RegLogActivity.INCORRECT_LOGIN_CREDITS ->
                    Toast.makeText(context, "Make sure you filled the form", Toast.LENGTH_SHORT).show()
            }
        }

        noAccountTextLogin.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun validateLogData(email: String, password: String) : Int{
        if(email == "" || password == ""){
            return RegLogActivity.INCORRECT_LOGIN_CREDITS
        }
        return RegLogActivity.GOOD
    }

    private fun setUpLayout() {
        val displayWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext().display!!.width
        } else {
            1500
        }

        logo.layoutParams.width = displayWidth
        logo.layoutParams.height = displayWidth * 6 / 14
        Log.d("height1: ", logo.layoutParams.height.toString())
        Log.d("width: : ", logo.layoutParams.width.toString())
    }

}