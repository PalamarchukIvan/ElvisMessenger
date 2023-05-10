package com.example.elvismessenger.fragments.settings.editingData

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.UserPersonalSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class PasswordSettingsFragment : Fragment() {

    private lateinit var newPassword: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newPassword = view.findViewById(R.id.new_password)
        UserPersonalSettings.livaDataInstance.observe(viewLifecycleOwner) {
            newPassword.setText(it.password)
        }
        newPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveData()
                true
            } else {
                false
            }
        }
    }

    private fun saveData() {
        val credential = EmailAuthProvider.getCredential(
        UserRepository.currentUser?.value!!.email,
        UserRepository.currentUser?.value!!.password
    )
        FirebaseAuth.getInstance().currentUser?.reauthenticate(credential) // Делается, что бы избежать "This operation is sensitive and requires recent authentication"
            ?.addOnSuccessListener {
                FirebaseAuth.getInstance().currentUser?.updatePassword(newPassword.text.toString())
                    ?.addOnSuccessListener {
                        val newUser = UserRepository.currentUser?.value
                        newUser?.password = newPassword.text.toString()
                        UserRepository.currentUser?.postValue(newUser)
                        UserRepository.getInstance().createOrUpdateUser(newUser!!)

                        val editor =
                            MainActivity.sp.edit()
                        editor?.putString(SettingsFragment.PASSWORD, newPassword.text.toString())
                        editor?.apply()

                        makeSuccess()
                    }
                    ?.addOnFailureListener {//ошибка смены пароля
                        makeWarning(it.message.toString())
                    }
            }
            ?.addOnFailureListener {//Ошибка переавторизации
                makeWarning(it.message.toString())
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_settings, container, false)
    }

    private fun makeWarning(error: String) {
        view?.findViewById<TextView>(R.id.password_settings_tv)?.text = error
        view?.findViewById<TextView>(R.id.password_settings_tv)?.setTextColor(Color.RED)
    }

    private fun makeSuccess() {
        view?.findViewById<TextView>(R.id.password_settings_tv)?.text = "Password was changed"
        view?.findViewById<TextView>(R.id.password_settings_tv)
            ?.setTextColor(Color.parseColor("#4682B4"))
    }
}