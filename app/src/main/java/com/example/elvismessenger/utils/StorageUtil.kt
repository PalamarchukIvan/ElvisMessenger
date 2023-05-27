package com.example.elvismessenger.utils

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class StorageUtil {
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
        ref.putBytes(imageBytes).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                onSuccess(it.toString())
            }
        }
    }
}