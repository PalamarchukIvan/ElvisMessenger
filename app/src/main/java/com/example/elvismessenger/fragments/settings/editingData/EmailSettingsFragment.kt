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

class EmailSettingsFragment : Fragment() {

    private lateinit var newEmail: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newEmail = view.findViewById(R.id.new_email)
        newEmail.setText(MainActivity.sp.getString(SettingsFragment.EMAIL, "test password"))
        newEmail.addTextChangedListener {
            saveData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_settings, container, false)
    }

    private fun saveData() {

        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.EMAIL, newEmail.text.toString())
        editor?.apply()
    }
}