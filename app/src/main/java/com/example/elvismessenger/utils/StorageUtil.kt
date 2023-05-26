package com.example.elvismessenger.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            )

    fun uploadMsgImg(imageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child("user_messages")
        ref.putBytes(imageBytes).addOnCanceledListener { onSuccess(ref.path) }
    }
}