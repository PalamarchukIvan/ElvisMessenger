package com.example.elvismessenger.fragments.settings.editingData

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.UserPersonalSettings
import com.google.firebase.auth.FirebaseAuth

class EmailSettingsFragment : Fragment() {

    private lateinit var newEmail: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newEmail = view.findViewById(R.id.new_email)

        UserPersonalSettings.livaDataInstance.observe(viewLifecycleOwner) {
            newEmail.setText(it.email)
        }
        newEmail.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                saveData()
            }
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

        val newUser = UserRepository.currentUser?.value
        newUser?.email = newEmail.text.toString()
        UserRepository.currentUser?.postValue(newUser)
        UserRepository.getInstance().createOrUpdateUser(newUser!!)
        FirebaseAuth.getInstance().currentUser?.updateEmail(newEmail.text.toString())
    }
}