package com.example.elvismessenger.fragments.settings.editingData

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.example.elvismessenger.utils.UserPersonalSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class EmailSettingsFragment : Fragment() {

    private lateinit var newEmail: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newEmail = view.findViewById(R.id.new_email)

        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        UserRepository.currentUser?.observe(viewLifecycleOwner){
            if(networkInfo != null && networkInfo.isConnected) {
                newEmail.setText(it.email)
            }
        }
        UserPersonalSettings.livaDataInstance.observe(viewLifecycleOwner) {
            if(networkInfo == null || !networkInfo.isConnected) {
                newEmail.setText(it.email)
            }
        }
        newEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveData()
                true
            } else {
                false
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

        if(!checkForProvider()) return

        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }


        val credential = EmailAuthProvider.getCredential(
            UserRepository.currentUser?.value!!.email,
            UserRepository.currentUser?.value!!.password
        )
        FirebaseAuth.getInstance().currentUser?.reauthenticate(credential) // Делается, что бы избежать "This operation is sensitive and requires recent authentication"
            ?.addOnSuccessListener {
                FirebaseAuth.getInstance().currentUser?.updateEmail(newEmail.text.toString())
                    ?.addOnSuccessListener {
                        makeSuccess()
                        val editor = MainActivity.sp.edit()
                        editor?.putString(SettingsFragment.EMAIL, newEmail.text.toString())
                        editor?.apply()

                        val newUser = UserRepository.currentUser?.value
                        newUser?.email = newEmail.text.toString()
                        UserRepository.currentUser?.postValue(newUser)
                        UserRepository.getInstance().createOrUpdateUser(newUser!!)
                    }
                    ?.addOnFailureListener {
                        makeWarning(it.message.toString())
                    }
            }
            ?.addOnFailureListener {
                makeWarning(it.message.toString())
            }


    }

    private fun checkForProvider(): Boolean{
        for(provider in FirebaseAuth.getInstance().currentUser!!.providerData) {
            if(provider.providerId != "firebase" && provider.providerId != "password") {
                Toast.makeText(requireContext(), "Illegal operation. You've signed in with google", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun makeWarning(error: String) {
        view?.findViewById<TextView>(R.id.email_settings_TV)?.text = error
        view?.findViewById<TextView>(R.id.email_settings_TV)?.setTextColor(Color.RED)
    }

    private fun makeSuccess() {
        view?.findViewById<TextView>(R.id.email_settings_TV)?.text = "Email was changed"
        view?.findViewById<TextView>(R.id.email_settings_TV)
            ?.setTextColor(Color.parseColor("#4682B4"))
    }
}