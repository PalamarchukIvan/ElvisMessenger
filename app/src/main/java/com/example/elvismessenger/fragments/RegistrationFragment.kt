package com.example.elvismessenger.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.RegLogActivity
import com.example.elvismessenger.db.UserRepository
import com.google.firebase.auth.FirebaseAuth

class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    private lateinit var regUsername: EditText
    private lateinit var regEmail: EditText
    private lateinit var regPassword: EditText
    private lateinit var regPasswordRepeat: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regUsername = view.findViewById(R.id.username_text_reg)
        regEmail = view.findViewById(R.id.email_text_reg)
        regPassword = view.findViewById(R.id.password_text_reg)
        regPasswordRepeat = view.findViewById(R.id.password_again_text_reg)

        val registrationButton: Button = view.findViewById(R.id.create_acc_button_reg)

        registrationButton.setOnClickListener {
            val username = regUsername.text.toString()
            val email = regEmail.text.toString()
            val password = regPassword.text.toString()
            val passwordRepeat = regPasswordRepeat.text.toString()

            Log.d("credits", "U: $username E:$email P:$password PE:$passwordRepeat")

            val validation = validateRegData(username, email, password, passwordRepeat)

            when (validation) {
                RegLogActivity.INCORRECT_USERNAME -> Toast.makeText(context, "username must be 6 characters long", Toast.LENGTH_SHORT).show()
                RegLogActivity.INCORRECT_EMAIL -> Toast.makeText(context, "Incorrect email", Toast.LENGTH_SHORT).show()
                RegLogActivity.INCORRECT_PASSWORD -> Toast.makeText(context, "password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                RegLogActivity.PASSWORDS_DO_NOT_MATCH -> Toast.makeText(context, "passwords must be same!", Toast.LENGTH_SHORT).show()
                RegLogActivity.GOOD -> {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful) {
                            // Запихиваем его в базу
                            FirebaseAuth.getInstance().currentUser.let { userFB ->
                                UserRepository.getInstance().createOrUpdateUser(
                                    UserRepository.toUserDB(userFB!!, password, username))

                                UserRepository.updateSharedPreferences(UserRepository.toUserDB(userFB, password, username))
                            }

                            // Перход на Welcome стрраничку
                            Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_welcomingEditProfileFragment)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "error: ${it.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateRegData(username: String, email: String, password: String, passwordRepeat: String): Int {
        if(username == "" || username.length < 6) {
            return RegLogActivity.INCORRECT_USERNAME
        }
        if(password.length < 6) {//1цифра, 1 латинская буква, 6+ симовлов
            return RegLogActivity.INCORRECT_PASSWORD
        }
        if(password != passwordRepeat) {
            return RegLogActivity.PASSWORDS_DO_NOT_MATCH
        }
        return RegLogActivity.GOOD
    }
}