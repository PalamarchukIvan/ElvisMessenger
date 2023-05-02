package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity

class SecuritySettingsFragment : Fragment(R.layout.fragment_security_settings) {

    private lateinit var changeEmail: ImageView
    private lateinit var changePassword: ImageView
    private lateinit var changePhoneNumber: ImageView

    private lateinit var phoneNumberVisibility: Spinner
    private lateinit var lastSeenVisibility: Spinner
    private lateinit var groupAddVisibility: Spinner

    override fun onResume() {
        super.onResume()

        SettingsFragment.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeEmail = view.findViewById(R.id.change_email_button)
        changePassword = view.findViewById(R.id.change_password_button)
        changePhoneNumber = view.findViewById(R.id.change_phone_button)


        // Адаптер для спинеров
        val visibilityAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.visibility, R.layout.spinner_item)
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1)

        // Спинеры
        phoneNumberVisibility = view.findViewById(R.id.phone_num_spinner)
        lastSeenVisibility = view.findViewById(R.id.last_seen_spinner)
        groupAddVisibility = view.findViewById(R.id.group_add_spinner)

        phoneNumberVisibility.adapter = visibilityAdapter
        lastSeenVisibility.adapter = visibilityAdapter
        groupAddVisibility.adapter = visibilityAdapter

        changeEmail.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_securitySettingsFragment_to_emailSettingsFragment)
        }

        changePassword.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_securitySettingsFragment_to_passwordSettingsFragment)
        }

        changePhoneNumber.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_securitySettingsFragment_to_phoneNumberSettingsFragment)
        }

        // Работа со спинерами

        phoneNumberVisibility.setSelection(MainActivity.sp.getInt(SettingsFragment.PHONE_NUMBER_VIS.toString(), 0))

        phoneNumberVisibility.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val editor = MainActivity.sp.edit()
                editor?.putInt(SettingsFragment.PHONE_NUMBER_VIS.toString(), position)
                editor?.apply()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                val editor = MainActivity.sp.edit()
                editor?.putInt(SettingsFragment.PHONE_NUMBER_VIS.toString(), 0)
                editor?.apply()
            }
        }

        lastSeenVisibility.setSelection(MainActivity.sp.getInt(SettingsFragment.LAST_SEEN_VIS.toString(), 0))

        lastSeenVisibility.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val editor = MainActivity.sp.edit()
                editor?.putInt(SettingsFragment.LAST_SEEN_VIS.toString(), position)
                editor?.apply()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                val editor = MainActivity.sp.edit()
                editor?.putInt(SettingsFragment.LAST_SEEN_VIS.toString(), 0)
                editor?.apply()
            }
        }

        groupAddVisibility.setSelection(MainActivity.sp.getInt(SettingsFragment.GROUP_ADD_VIS.toString(), 0))

        groupAddVisibility.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val editor = MainActivity.sp.edit()
                editor?.putInt(SettingsFragment.GROUP_ADD_VIS.toString(), position)
                editor?.apply()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                val editor = MainActivity.sp.edit()
                editor?.putInt(SettingsFragment.GROUP_ADD_VIS.toString(), 0)
                editor?.apply()
            }
        }
    }
}
