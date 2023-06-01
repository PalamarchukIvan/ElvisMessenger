package com.example.elvismessenger.fragments.settings.editingData

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.UserPersonalSettings

class PhoneNumberSettingsFragment : Fragment() {

    private lateinit var newPhoneNumber: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newPhoneNumber = view.findViewById(R.id.new_phone_number)

        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        UserRepository.currentUser?.observe(viewLifecycleOwner) {
            if (networkInfo != null && networkInfo.isConnected) {
                newPhoneNumber.setText(it.phoneNumber)
            }
        }
        UserPersonalSettings.livaDataInstance.observe(viewLifecycleOwner) {
            if (networkInfo == null || !networkInfo.isConnected) {
                newPhoneNumber.setText(it.phoneNumber)
            }
        }

        newPhoneNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                saveData()
            }
        }
    }

    private fun saveData() {

        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }


        val editor =
            MainActivity.sp.edit()
        editor.putString(SettingsFragment.PHONE_NUMBER, newPhoneNumber.text.toString())
        editor.apply()

        val newUser = UserRepository.currentUser?.value
        newUser?.password = newPhoneNumber.text.toString()

        UserRepository.currentUser?.postValue(newUser)
        UserRepository.getInstance().createOrUpdateUser(newUser!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_number_settings, container, false)
    }
}