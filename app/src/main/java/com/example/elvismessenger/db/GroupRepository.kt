package com.example.elvismessenger.db

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.utils.FCMSender
import com.example.elvismessenger.utils.NotificationService
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
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
                Group(groupReference.key!!, groupName, userList = userList.stream()
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
                        id = groupReference.key,
                        photo = savedGroup?.groupPhoto,
                        name = groupName
                    ))
                }
            }
        }

    }

    fun updateGroup(newGroup: Group) {
        getGroupById(newGroup.id).setValue(newGroup)
    }

    fun updateWhoIsWriting(add: Boolean, username: String, group: Group) {
        getGroupById(group.id).get().addOnSuccessListener {
            val actualGroup = it.getValue(Group::class.java)!!
            if(add) {
                actualGroup.whoAreWriting.add(username)
            } else {
                actualGroup.whoAreWriting.remove(username)
            }
            if(add) {
                getGroupById(group.id).child("whoAreWriting").get().addOnSuccessListener {
                    for(i in it.children) {//Второй уровень защиты
                        if(i.getValue(String::class.java)!! == username){
                            return@addOnSuccessListener
                        }
                    }
                    getGroupById(group.id).child("whoAreWriting").setValue(actualGroup.whoAreWriting)
                }
            } else {
                getGroupById(group.id).child("whoAreWriting").setValue(actualGroup.whoAreWriting)
            }
        }
    }

    fun getGroupWhoAreWriting(id: String) = getGroupById(id).child("whoAreWriting")

    fun getGroupById(id: String) = FirebaseDatabase.getInstance().getReference("groups").child(id)

    fun getGroupMessages(id: String) = getGroupById(id).child("messages")
    fun getGroupUsers(id: String) = getGroupById(id).child("userList")

    fun sendMessage(msg: ChatMessage, currentUser: User, group: Group, chatQuery: Query, context: Context, errorHandler: (DatabaseError?) -> Unit) {

        // Для записи этого же сообщения в список последних сообщений всех юзеров
        for (uid in group.userList) {
            val chatItemMsg = ChatListFragment.ChatItem(msg.text, System.currentTimeMillis(), false, id = group.id, name = group.groupName, photo = group.groupPhoto, isGroup = true)
            val latestMsgRef = ChatRepository.getInstance().getOpenToUserChat(uid, group.id)
            latestMsgRef.setValue(chatItemMsg)
            if(currentUser.uid != uid) {
                GlobalScope.launch {
                    UserRepository.getInstance().getUserByUID(uid).snapshots.collect {
                        val otherUser = it.getValue(User::class.java)
                        FCMSender.pushNotification(
                            context,
                            otherUser!!.cloudToken,
                            currentUser.username,
                            msg.text,
                            currentUser.uid,
                            otherUser.uid,
                            NotificationService.ACTION_NOTIFICATION
                        )

                    }
                }
            }
        }

        chatQuery.ref.push().setValue(msg) { error, _ ->
            error?.let {
                errorHandler.invoke(it)
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