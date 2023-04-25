package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import com.example.elvismessenger.R

class SettingsFragment : Fragment() {
    private lateinit var toChatSettings: ImageView
    private lateinit var toSecuritySettings: ImageView
    private lateinit var toNotificationsSettings: ImageView
    private lateinit var toLanguageSettings: ImageView
    private lateinit var toBanListSettings: ImageView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toChatSettings = view.findViewById(R.id.chat_settings_button)
        toSecuritySettings = view.findViewById(R.id.security_settings_button)
        toNotificationsSettings = view.findViewById(R.id.notifications_settings_button)
        toLanguageSettings = view.findViewById(R.id.language_settings)
        toBanListSettings = view.findViewById(R.id.ban_list_button)


        toChatSettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_chatSettingsFragment)
        }

        toSecuritySettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_securitySettingsFragment)
        }

        toNotificationsSettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_notificationsSettingsFragment)
        }

        toLanguageSettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_languageSettingsFragment)
        }

        toBanListSettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_banListSettingsFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}