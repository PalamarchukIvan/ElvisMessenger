package com.example.elvismessenger.fragments.settings

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.UserPersonalSettings
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SettingsFragment : Fragment() {
    private lateinit var toChatSettings: ImageView
    private lateinit var toSecuritySettings: ImageView
    private lateinit var toNotificationsSettings: ImageView
    private lateinit var toLanguageSettings: ImageView
    private lateinit var toBanListSettings: ImageView
    private lateinit var theme: SwitchCompat
    private lateinit var toEditProfile: AppCompatButton
    private lateinit var currentLanguage: TextView
    private lateinit var userPhoto: CircleImageView

    companion object {
        //Инициализация SharedPreferances + LiveData
        private val sp = MainActivity.sp
        private val userSettings = UserPersonalSettings.livaDataInstance

        const val SHARED_PREFERENCES: String = "settings"
        const val THEME: String = "theme"
        const val TEXT_SIZE: String = "text size"
        const val NOTIFICATION_VOLUME: String = "notify_volume"

        const val EMAIL: String = "email"
        const val PASSWORD: String = "password"
        const val PHONE_NUMBER: String = "phone number"

        const val PHONE_NUMBER_VIS: Int = 0
        const val LAST_SEEN_VIS: Int = 1
        const val GROUP_ADD_VIS: Int = 2

        const val LANGUAGE_SELECTED: String = "language"
        const val USERNAME: String = "username"
        const val STATUS: String = "status"
        const val ABOUT: String = "about"
        const val PHOTO = "photo"

        internal fun loadData() {
            val newSettings = userSettings.value

            newSettings?.phoneNumber = sp.getString(PHONE_NUMBER, "").toString()
            newSettings?.password = sp.getString(PASSWORD, "").toString()
            newSettings?.email = sp.getString(EMAIL, "").toString()
            newSettings?.ifDarkTheme = sp.getBoolean(THEME, false)
            newSettings?.textSize = sp.getInt(TEXT_SIZE, 18)
            newSettings?.language = sp.getString(LANGUAGE_SELECTED, "English").toString()
            newSettings?.username = sp.getString(USERNAME, "").toString()
            newSettings?.about = sp.getString(ABOUT, "").toString()
            newSettings?.status = sp.getString(STATUS, "").toString()
            newSettings?.notificationVolume = sp.getInt(NOTIFICATION_VOLUME, 100)
            newSettings?.photo = sp.getString(PHOTO, "").toString()

            userSettings.postValue(newSettings)
        }
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
        userPhoto = view.findViewById(R.id.settings_user_photo)

        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if(networkInfo != null && networkInfo.isConnected) {
                UserRepository.currentUser?.observe(viewLifecycleOwner) {
                    theme.isChecked = userSettings.value?.ifDarkTheme ?: false
                    currentLanguage.text = userSettings.value?.language

                    view.findViewById<TextView>(R.id.settings_username).text = it.username
                    view.findViewById<TextView>(R.id.settings_status).text = it.status
                    if (it.photo.isNotBlank()) {
                        userPhoto.let { photo ->
                            Picasso.get()
                                .load(it.photo)
                                .into(photo)
                        }
                    } else {
                        userPhoto.let { photo ->
                            Picasso.get()
                                .load(R.drawable.dornan)
                                .into(photo)
                        }
                    }
                }
            } else {
            userSettings.observe(viewLifecycleOwner) {
                theme.isChecked = userSettings.value?.ifDarkTheme ?: false
                currentLanguage.text = userSettings.value?.language

                view.findViewById<TextView>(R.id.settings_username).text =
                    userSettings.value?.username
                view.findViewById<TextView>(R.id.settings_status).text = userSettings.value?.status
                if (it.photo.isNotBlank()) {
                    userPhoto.let { photo ->
                        Picasso.get()
                            .load(it.photo)
                            .into(photo)
                    }
                } else {
                    userPhoto.let { photo ->
                        Picasso.get()
                            .load(R.drawable.dornan)
                            .into(photo)
                    }
                }
            }
        }

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

        loadData()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}