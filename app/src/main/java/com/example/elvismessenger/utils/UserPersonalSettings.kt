package com.example.elvismessenger.utils

import androidx.lifecycle.MutableLiveData


class UserPersonalSettings private constructor() {
    var email: String = ""
    var password: String = ""
    var phoneNumber: String = ""
    var ifDarkTheme: Boolean = false
    var textSize: Int = 0
    var language: String = ""
    var username: String = "username"
    var about: String = ""
    var status: String = "no status"
    var notificationVolume: Int = 100

    companion object {
        private var instance: UserPersonalSettings = UserPersonalSettings()
        val livaDataInstance = MutableLiveData(instance)
    }
}