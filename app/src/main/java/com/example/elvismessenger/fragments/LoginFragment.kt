package com.example.elvismessenger.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.activities.RegLogActivity
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.SettingsFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        const val GOOGLE_SIGN_IN_TAG = "GoogleSignIn"
        const val GOOGLE_SIGN_IN = 111
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var logEmail: EditText
    private lateinit var logPassword: EditText
    private lateinit var logo: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logo = view.findViewById(R.id.login_logo_img)

        val loginButton: Button = view.findViewById(R.id.login_button_login)
        val noAccountTextLogin: TextView = view.findViewById(R.id.no_account_text_login)

        logEmail = view.findViewById(R.id.email_text_login)
        logPassword = view.findViewById(R.id.password_text_login)

        val googleSignInButton: SignInButton = view.findViewById(R.id.google_sign_in_button)

        setUpLayout()

        // Sign in через гугл
        auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(requireContext(), getGSO())
        googleSignInButton.setOnClickListener {
            googleSignIn()
        }

        // Логирование через емейл и пароль
        loginButton.setOnClickListener {

            val email = logEmail.text.toString()
            val password = logPassword.text.toString()

            val validation = validateLogData(email, password)

            when(validation) {
                RegLogActivity.GOOD -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                UserRepository.updateSharedPreferances(UserRepository.toUserDB(FirebaseAuth.getInstance().currentUser!!, uPassword = password))
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainActivity)
                                activity?.finish()
                            } else {
                                Toast.makeText(context, "user with such credits does not exist", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                RegLogActivity.INCORRECT_LOGIN_CREDITS ->
                    Toast.makeText(context, "Make sure you filled the form", Toast.LENGTH_SHORT).show()
            }
        }

        noAccountTextLogin.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.d(GOOGLE_SIGN_IN_TAG, e.message.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    var user: User?

                    lifecycleScope.launch {
                        UserRepository.getInstance()
                            .getUserByUID(FirebaseAuth.getInstance().currentUser!!.uid).snapshots.collect {
                                user = it.getValue(User::class.java)
                                if (user != null) {
                                    view?.let {
                                        Navigation.findNavController(it)
                                            .navigate(R.id.action_loginFragment_to_mainActivity)
                                        activity?.finish()
                                    }
                                } else {
                                    // Запихиваем его в базу
                                    FirebaseAuth.getInstance().currentUser.let { userFB ->
                                        UserRepository.getInstance().createOrUpdateUser(
                                            UserRepository.toUserDB(userFB!!)
                                        )
                                    }

                                    view?.let {
                                        Navigation.findNavController(it)
                                            .navigate(R.id.action_loginFragment_to_welcomingEditProfileFragment)
                                    }
                                }
                            }
                    }

                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong during authentication with Google",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    private fun getGSO(): GoogleSignInOptions {
        return  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private fun validateLogData(email: String, password: String) : Int{
        if(email == "" || password == ""){
            return RegLogActivity.INCORRECT_LOGIN_CREDITS
        }
        return RegLogActivity.GOOD
    }

    private fun setUpLayout() {
        val displayWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext().display!!.width
        } else {
            1500
        }

        logo.layoutParams.width = displayWidth - 80
        logo.layoutParams.height = displayWidth * 6 / 14 - 30
        Log.d("height1: ", logo.layoutParams.height.toString())
        Log.d("width: : ", logo.layoutParams.width.toString())
    }

}