package com.example.elvismessenger.db

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings.Global
import android.util.Log
import com.example.elvismessenger.fragments.ChatListFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.streams.toList

object GroupRepository {
    fun createGroup(userList: MutableList<User>, groupName: String?, groupPhoto: Uri?, context: Context?) {
        val groupReference = FirebaseDatabase.getInstance().getReference("groups").push()
        if(groupPhoto != null) {
            //Сжатие
            val bmp = MediaStore.Images.Media.getBitmap(context?.contentResolver, groupPhoto);
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val fileInBytes = baos.toByteArray()

            addOrUpdatePhoto(
                fileInBytes,
                groupReference.key!!,
                Group(groupName, userList = userList.stream()
                    .map {
                        it.uid
                    }.toList()),
                groupReference
            )
        }
        GlobalScope.launch {
            groupReference.snapshots.collect {
                val savedGroup = it.getValue(Group::class.java)
                for(user in userList) {
                    val newLatestMessageReference = ChatRepository.getInstance().getOpenToUserChat(user.uid).child(groupReference.key!!)
                    newLatestMessageReference.setValue(ChatListFragment.ChatItem(
                        text = "",
                        time = System.currentTimeMillis(),
                        isNew = true,
                        isGroup = true,
                        groupId = groupReference.key,
                        groupPhoto = savedGroup?.groupPhoto,
                        groupName = groupName
                    ))
                }
            }
        }

    }

    fun addOrUpdatePhoto(fileInBytes: ByteArray, groupId: String, group: Group, groupReference: DatabaseReference) {
        val photoNodeRef = FirebaseStorage
                                .getInstance()
                                .getReference("group_photos")
                                .child(groupId)

        Thread {
            photoNodeRef.putBytes(fileInBytes).addOnSuccessListener {
                Thread {
                    photoNodeRef.downloadUrl.addOnSuccessListener {
                        group.groupPhoto = it.toString()
                        groupReference.setValue(group)
                    }.addOnFailureListener {
                        Log.d("Error with photo ", it.message.toString())
                    }
                }.start()
            }.addOnFailureListener {
                Log.d("Error with photo ", it.message.toString())
            }
        }.start()
    }
}