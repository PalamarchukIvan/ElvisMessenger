package com.example.elvismessenger.fragments.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.UserPersonalSettings
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private lateinit var newAbout: EditText
    private lateinit var newStatus: EditText
    private lateinit var newName: EditText
    private lateinit var newPhotoBtn: ImageView
    private lateinit var currentPhoto: ImageView
    private lateinit var submitBtn: AppCompatButton

    companion object {
        const val NEW_PHOTO_REQ_CODE = 123
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newStatus = view.findViewById(R.id.new_status)
        newAbout = view.findViewById(R.id.new_about)
        newName = view.findViewById(R.id.new_username)
        submitBtn = view.findViewById(R.id.submit_profile_btn)
        currentPhoto = view.findViewById(R.id.current_photo_edit_profile)
        newPhotoBtn = view.findViewById(R.id.change_photo_btn)


        UserRepository.currentUser?.observe(viewLifecycleOwner) {
            newStatus.setText(it.status)
            newAbout.setText(it.about)
            newName.setText(it.username)
            currentPhoto.let { photo ->
                if(it.photo.isNotBlank()) {
                    Picasso.get()
                        .load(it.photo)
                        .into(photo)
                } else {
                    Picasso.get()
                        .load(R.drawable.dornan)
                        .into(photo)
                }
            }
            submitBtn.isVisible = false
        } ?: UserPersonalSettings.livaDataInstance.observe(viewLifecycleOwner) {
            newStatus.setText(it.status)
            newAbout.setText(it.about)
            newName.setText(it.username)
            currentPhoto.let { photo ->
                if(it.photo.isNotBlank()) {
                    Picasso.get()
                        .load(it.photo)
                        .into(photo)
                } else {
                    Picasso.get()
                        .load(R.drawable.dornan)
                        .into(photo)
                }
            }
            submitBtn.isVisible = false
        }

        newStatus.addTextChangedListener {
            submitBtn.isVisible = true
        }

        newAbout.addTextChangedListener {
            submitBtn.isVisible = true
        }

        newName.addTextChangedListener {
            submitBtn.isVisible = true
        }

        submitBtn.setOnClickListener {
            saveData()
            submitBtn.isVisible = false
        }

        newPhotoBtn.setOnClickListener {
            val iGallery = Intent(Intent.ACTION_PICK)
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iGallery, NEW_PHOTO_REQ_CODE)
            submitBtn.isVisible = true
        }
    }

    //Прием фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            if(requestCode == NEW_PHOTO_REQ_CODE) {
                currentPhoto.setImageURI(data?.data)
                saveImage(data?.data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    private fun saveData() {

        if(newName.text.toString().length < 6) {
            Toast.makeText(requireContext(), "New username is badly formatted", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = UserRepository.currentUser?.value
        newUser?.status = newStatus.text.toString()
        newUser?.username = newName.text.toString()
        newUser?.about = newAbout.text.toString()

        UserRepository.currentUser?.postValue(newUser)

        UserRepository.currentUser?.value?.apply {
            UserRepository.getInstance().createOrUpdateUser(this)
        }

        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.STATUS, newStatus.text.toString())
        editor?.putString(SettingsFragment.ABOUT, newAbout.text.toString())
        editor?.putString(SettingsFragment.USERNAME, newName.text.toString())
        editor?.putString(SettingsFragment.PHOTO, UserRepository.currentUser!!.value!!.photo)
        editor?.apply()
    }

    private fun saveImage(imageUri: Uri?) {
        imageUri?.apply {
            UserRepository.currentUser?.apply {
                UserRepository.getInstance().addOrUpdateUserPhoto(imageUri)
            }
        }
    }

}