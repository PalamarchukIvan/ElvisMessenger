package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.utils.UserPersonalSettings

class EditProfileFragment : Fragment() {

    private lateinit var newAbout: EditText
    private lateinit var newStatus: EditText
    private lateinit var newName: EditText
    private lateinit var submitBtn: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newStatus = view.findViewById(R.id.new_status)
        newAbout = view.findViewById(R.id.new_about)
        newName = view.findViewById(R.id.new_username)
        submitBtn = view.findViewById(R.id.submit_profile_btn)

        newStatus.setText(MainActivity.sp.getString(SettingsFragment.STATUS, "no status"))
        newStatus.addTextChangedListener {
            submitBtn.isVisible = true

        }

        newAbout.setText(MainActivity.sp.getString(SettingsFragment.ABOUT, "no about"))
        newAbout.addTextChangedListener {
            submitBtn.isVisible = true
        }

        newName.setText(MainActivity.sp.getString(SettingsFragment.USERNAME, "username"))
        newName.addTextChangedListener {
            submitBtn.isVisible = true
        }

        submitBtn.setOnClickListener {
            saveData()
            submitBtn.isVisible = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    private fun saveData() {
        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.STATUS, newStatus.text.toString())
        editor?.putString(SettingsFragment.ABOUT, newAbout.text.toString())
        editor?.putString(SettingsFragment.USERNAME, newName.text.toString())
        editor?.apply()
    }

}