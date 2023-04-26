package com.example.elvismessenger.fragments.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.utils.UserPersonalSettings

class SettingsFragment : Fragment() {
    private lateinit var toChatSettings: ImageView
    private lateinit var toSecuritySettings: ImageView
    private lateinit var toNotificationsSettings: ImageView
    private lateinit var toLanguageSettings: ImageView
    private lateinit var toBanListSettings: ImageView
    private lateinit var theme: SwitchCompat


    //Инициализация SharedPreferances
    val sp = MainActivity.sp

    private val userSettings: UserPersonalSettings = UserPersonalSettings.getInstance()

    companion object {
        const val SHARED_PREFERENCES: String = "settings"
        const val THEME: String = "theme"
        const val TEXT_SIZE: String = "text size"

        const val EMAIL: String = "email"
        const val PASSWORD: String = "password"
        const val PHONE_NUMBER: String = "phone number"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toChatSettings = view.findViewById(R.id.chat_settings_button)
        toSecuritySettings = view.findViewById(R.id.security_settings_button)
        toNotificationsSettings = view.findViewById(R.id.notifications_settings_button)
        toLanguageSettings = view.findViewById(R.id.language_settings)
        toBanListSettings = view.findViewById(R.id.ban_list_button)
        theme = view.findViewById(R.id.if_dark_theme_button)
        theme.isChecked = sp.getBoolean(THEME, false)

        toChatSettings.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_chatSettingsFragment)
        }

        toSecuritySettings.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_securitySettingsFragment)
        }

        toNotificationsSettings.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_notificationsSettingsFragment)
        }

        toLanguageSettings.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_languageSettingsFragment)
        }

        toBanListSettings.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_banListSettingsFragment)
        }

        theme.setOnClickListener {
            val editor = sp.edit()
            editor.putBoolean(THEME, theme.isChecked)
            editor.apply()
        }

        loadData()
    }

    private fun loadData() {
        userSettings.phoneNumber = sp.getString(PHONE_NUMBER, "").toString()
        userSettings.password = sp.getString(PASSWORD, "").toString()
        userSettings.email = sp.getString(EMAIL, "").toString()
        userSettings.ifDarkTheme = sp.getBoolean(THEME, false)
        userSettings.textSize
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}