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

class PhoneNumberSettingsFragment : Fragment() {

    private lateinit var newPhoneNumber: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newPhoneNumber = view.findViewById(R.id.new_phone_number)
        newPhoneNumber.setText(MainActivity.sp.getString(SettingsFragment.PHONE_NUMBER, "test phone_number"))
        newPhoneNumber.addTextChangedListener {
            saveData()
        }
    }

    private fun saveData() {
        val editor =
            MainActivity.sp.edit()
        editor.putString(SettingsFragment.PHONE_NUMBER, newPhoneNumber.text.toString())
        editor.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_number_settings, container, false)
    }
}