package com.example.elvismessenger.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.RegLogActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.EditProfileFragment
import com.squareup.picasso.Picasso

class WelcomingEditProfileFragment : Fragment(R.layout.fragment_welcoming_edit_profile) {

    private lateinit var userPfp: ImageView
    private lateinit var editUsername: EditText
    private lateinit var editStatus: EditText
    private lateinit var editAbout: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButton: Button = view.findViewById(R.id.edit_profile_button)

        userPfp = view.findViewById(R.id.user_pfp_image)
        val changePfpButton: ImageView = view.findViewById(R.id.change_photo_btn)

        editUsername = view.findViewById(R.id.edit_username)
        editStatus = view.findViewById(R.id.edit_status)
        editAbout = view.findViewById(R.id.edit_about)

        // Подгрузка уже введеных значений
        UserRepository.currentUser?.observe(viewLifecycleOwner) {
            editUsername.setText(it.username)
            userPfp.let { photo ->
                if (it.photo.isNotBlank()) {
                    Picasso.get()
                        .load(it.photo)
                        .into(photo)
                } else {
                    Picasso.get()
                        .load(R.drawable.no_pfp)
                        .into(photo)
                }
            }
        }

        confirmButton.setOnClickListener {
            val username = editUsername.text.toString()
            val status = editStatus.text.toString()
            val about = editAbout.text.toString()

            when (validateData(username, status, about)) {
                RegLogActivity.INCORRECT_USERNAME_SIZE -> Toast.makeText(
                    requireContext(),
                    "Username name should be greater than 6 and less than 20",
                    Toast.LENGTH_SHORT
                ).show()
                RegLogActivity.INCORRECT_STATUS_SIZE -> Toast.makeText(
                    requireContext(),
                    " Status should be less than 10 characters long",
                    Toast.LENGTH_SHORT
                ).show()
                RegLogActivity.INCORRECT_ABOUT_SIZE -> Toast.makeText(
                    requireContext(),
                    "About should be less than 128 characters long",
                    Toast.LENGTH_SHORT
                ).show()
                RegLogActivity.GOOD -> {
                    saveData()
                    Navigation.findNavController(view).navigate(R.id.action_welcomingEditProfileFragment_to_mainActivity)
                    activity?.finish()
                }
            }
        }

        changePfpButton.setOnClickListener {
            val iGallery = Intent(Intent.ACTION_PICK)
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iGallery, EditProfileFragment.NEW_PHOTO_REQ_CODE)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UserRepository.initCurrentUser()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == EditProfileFragment.NEW_PHOTO_REQ_CODE) {

                data?.data?.let {
                    val resultUri: Uri = saveImage(data.data)
                    userPfp.setImageURI(resultUri)
                }?: {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong. Try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateData(username: String, status: String, bio: String): Int {

        if (username.length < 6 ||  username.length > 20)
            return RegLogActivity.INCORRECT_USERNAME_SIZE
        if(status.length > 10)
            return RegLogActivity.INCORRECT_STATUS_SIZE
        if(bio.length > 128)
            return RegLogActivity.INCORRECT_ABOUT_SIZE

        return RegLogActivity.GOOD
    }

    private fun saveImage(imageUri: Uri?): Uri {
        var uri: Uri? = null
        imageUri?.apply {
            UserRepository.currentUser?.apply {
                uri = UserRepository.getInstance().addOrUpdateUserPhoto(imageUri, requireContext())
            }
        }
        return uri!!
    }

    private fun saveData() {
        val newUser = UserRepository.currentUser.value
        newUser?.username = editUsername.text.toString()
        newUser?.status = editStatus.text.toString()
        newUser?.about = editAbout.text.toString()
        newUser?.lastSeen = System.currentTimeMillis()
        newUser?.isActive = true

        UserRepository.currentUser.postValue(newUser)

        UserRepository.currentUser.value?.apply {
            UserRepository.getInstance().createOrUpdateUser(this)
        }
    }
}