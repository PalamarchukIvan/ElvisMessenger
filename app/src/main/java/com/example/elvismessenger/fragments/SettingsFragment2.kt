package com.example.elvismessenger.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.elvismessenger.R

class SettingsFragment2 : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}