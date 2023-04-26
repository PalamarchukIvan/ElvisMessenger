package com.example.elvismessenger.fragments.settings.editingData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.fragments.settings.SettingsFragment

class PasswordSettingsFragment : Fragment() {

    private lateinit var newPassword: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newPassword = view.findViewById(R.id.new_password)
        newPassword.setText(MainActivity.sp.getString(SettingsFragment.PASSWORD, "test email"))
        newPassword.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                saveData()
            }
        }
    }

    private fun saveData() {
        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.PASSWORD, newPassword.text.toString())
        editor?.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_settings, container, false)
    }
}