package com.example.elvismessenger.fragments.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.utils.UserPersonalSettings

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

        UserPersonalSettings.livaDataInstance.observe(viewLifecycleOwner) {
            newStatus.setText(it.status)
            newAbout.setText(it.about)
            newName.setText(it.username)
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
                saveImage(data)
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
        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.STATUS, newStatus.text.toString())
        editor?.putString(SettingsFragment.ABOUT, newAbout.text.toString())
        editor?.putString(SettingsFragment.USERNAME, newName.text.toString())
        editor?.apply()
    }

    private fun saveImage(data: Intent?) {
        //Пока нет смысла, нет базы а в файлах сохранять мы не будем
    }

}