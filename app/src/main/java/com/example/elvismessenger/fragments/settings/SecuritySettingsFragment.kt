package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.elvismessenger.R

class SecuritySettingsFragment : Fragment() {

    private lateinit var changeEmail: ImageView
    private lateinit var changePassword: ImageView
    private lateinit var changePhoneNumber: ImageView

    private lateinit var phoneNumberVisibility: TextView
    private lateinit var lastSeenVisibility: TextView
    private lateinit var groupAddConstraints: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeEmail = view.findViewById(R.id.change_email_button)
        changePassword = view.findViewById(R.id.change_password_button)
        changePhoneNumber = view.findViewById(R.id.change_phone_button)
        phoneNumberVisibility = view.findViewById(R.id.phone_number_vis)
        lastSeenVisibility = view.findViewById(R.id.last_seen_vis)
        groupAddConstraints = view.findViewById(R.id.group_add_vis)


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

        phoneNumberVisibility.setOnClickListener {
            //Всплывающее окно
        }

        lastSeenVisibility.setOnClickListener {
            //Всплывающее окно
        }

        groupAddConstraints.setOnClickListener {
            //Всплывающее окно
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security_settings, container, false)
    }
}
