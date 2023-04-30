package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
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
    private lateinit var toEditProfile: AppCompatButton
    private lateinit var currentLanguage: TextView


    //Инициализация SharedPreferances
    val sp = MainActivity.sp

    private val userSettings = UserPersonalSettings.livaDataInstance

    companion object {
        const val SHARED_PREFERENCES: String = "settings"
        const val THEME: String = "theme"
        const val TEXT_SIZE: String = "text size"

        const val EMAIL: String = "email"
        const val PASSWORD: String = "password"
        const val PHONE_NUMBER: String = "phone number"

        const val PHONE_NUMBER_VIS: Int = 0
        const val LAST_SEEN_VIS: Int = 1
        const val GROUP_ADD_VIS: Int = 2

        const val LANGUAGE_SELECTED:String = "language"
        const val USERNAME: String = "username"
        const val STATUS: String = "status"
        const val ABOUT: String = "about"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toChatSettings = view.findViewById(R.id.chat_settings_button)
        toSecuritySettings = view.findViewById(R.id.security_settings_button)
        toNotificationsSettings = view.findViewById(R.id.notifications_settings_button)
        toLanguageSettings = view.findViewById(R.id.language_settings)
        toBanListSettings = view.findViewById(R.id.ban_list_button)
        theme = view.findViewById(R.id.if_dark_theme_button)
        toEditProfile = view.findViewById(R.id.to_edit_profile_btn)
        currentLanguage = view.findViewById(R.id.current_language)

        theme.isChecked = userSettings.value?.ifDarkTheme ?: false
        currentLanguage.text = userSettings.value?.language

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

        toEditProfile.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_editProfileFragment)
        }


        view.findViewById<TextView>(R.id.settings_username).text = userSettings.value?.username
        view.findViewById<TextView>(R.id.settings_status).text = userSettings.value?.status

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}