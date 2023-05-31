package com.example.elvismessenger.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

// object
object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(
                "users_chat_images/${
                    FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException(
                        "UID is null."
                    )
                }"
            )

    // onSuccess это callback функция
    fun uploadMsgImg(imageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child(UUID.nameUUIDFromBytes(imageBytes).toString())
        ref.putBytes(imageBytes).addOnCompleteListener {
            if (it.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener {
                    onSuccess(it.toString())
                }
            } else {
                Log.d("ERROR", it.exception?.message.toString())
            }
        }
    }

    fun compressImg(selectedImageBmp: Bitmap): ByteArray? {
        return if (selectedImageBmp.byteCount >= 8_000_000) {
            null
        } else {
            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
            Log.d("BYTES", outputStream.size().toString())

            outputStream.toByteArray()
        }
    }

}