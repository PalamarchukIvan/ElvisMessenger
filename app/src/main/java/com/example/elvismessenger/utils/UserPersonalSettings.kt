package com.example.elvismessenger.utils

import androidx.lifecycle.MutableLiveData


class UserPersonalSettings private constructor() {
    var email: String = "test email"
    var password: String = "test pwd"
    var phoneNumber: String = "123321"
    var ifDarkTheme: Boolean = false
    var textSize: Int = 18
    var language: String = "English"
    var username: String = "no username"
    var about: String = "no about"
    var status: String = "no status"
    var notificationVolume: Int = 100
    var photo: String = ""

    companion object {
        private var instance: UserPersonalSettings = UserPersonalSettings()
        val livaDataInstance = MutableLiveData(instance)
    }

    override fun toString(): String {
        return "UserPersonalSettings(email='$email', password='$password', phoneNumber='$phoneNumber', ifDarkTheme=$ifDarkTheme, textSize=$textSize, language='$language', username='$username', about='$about', status='$status', notificationVolume=$notificationVolume, photo='$photo')"
    }
}