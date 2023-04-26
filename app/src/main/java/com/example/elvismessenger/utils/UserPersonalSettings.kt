package com.example.elvismessenger.utils


class UserPersonalSettings private constructor() {
    var email: String = ""
    var password: String = ""
    var phoneNumber: String = ""
    var ifDarkTheme: Boolean = false
    var textSize: Int = 0

    companion object {
        private var instance: UserPersonalSettings? = null

        fun getInstance(): UserPersonalSettings {
            if (instance == null) {
                instance = UserPersonalSettings()
            }
            return instance!!
        }
    }
}