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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.streams.toList

object GroupRepository {
    fun createGroup(userList: MutableList<User>, groupName: String?, groupPhoto: Uri?, context: Context?) {
        val groupReference = FirebaseDatabase.getInstance().getReference("groups").push()
        val group = Group(groupReference.key!!, groupName, userList = userList.stream()
            .map {
                it.uid
            }.toList())
        groupReference.setValue(group)
        if(groupPhoto != null) {
            //Сжатие
            val bmp = MediaStore.Images.Media.getBitmap(context?.contentResolver, groupPhoto);
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val fileInBytes = baos.toByteArray()

            addOrUpdatePhoto(
                fileInBytes,
                group,
                groupReference
            )
        }
        GlobalScope.launch {
            groupReference.snapshots.collect {
                val savedGroup = it.getValue(Group::class.java)
                for(user in userList) {
                    val newLatestMessageReference = ChatRepository.getInstance().getOpenToUserChat(user.uid).child(groupReference.key!!)
                    newLatestMessageReference.get().addOnSuccessListener {
                        if(it.getValue(Group::class.java) == null) {
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
        }

    }

    fun updateGroupName(id: String, newName: String) {
        getGroupById(id).get().addOnSuccessListener {
            val newGroup = it.getValue<Group>()!!
            newGroup.groupName = newName
            getGroupById(id).setValue(newGroup)
        }
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

    fun deleteMessage(groupId: String, msgId: String, onSuccess: () -> Unit) {
        getGroupMessages(groupId).child(msgId).removeValue().addOnSuccessListener {
            onSuccess.invoke()
        }
    }

    fun deleteUserFromGroup(uid: String, groupId: String) {
        getGroupUsers(groupId).get().addOnSuccessListener {
            val userList = mutableListOf<String>()
            for (i in it.children) {
                if(i.getValue(String::class.java)!! != uid) {
                    userList.add(i.getValue(String::class.java)!!)
                }
            }
            getGroupUsers(groupId).setValue(userList)
            UserRepository.getInstance().getUserLatestMessages(uid).child(groupId).removeValue()
        }
    }

    fun sendMessage(msg: ChatMessage, currentUser: User, group: Group, chatQuery: Query, context: Context, errorHandler: (DatabaseError?) -> Unit) {

        // Для записи этого же сообщения в список последних сообщений всех юзеров
        for (uid in group.userList) {
            val chatItemMsg = ChatListFragment.ChatItem(msg.text, System.currentTimeMillis(), uid != currentUser.uid, id = group.id, name = group.groupName, photo = group.groupPhoto, isGroup = true)
            val latestMsgRef = ChatRepository.getInstance().getOpenToUserChat(uid, group.id)
            if (msg.img.isNotEmpty()) chatItemMsg.text = "Photo"
            latestMsgRef.setValue(chatItemMsg).addOnSuccessListener {
                if(currentUser.uid != uid) {
                    UserRepository.getInstance().getUserByUID(uid).get().addOnSuccessListener {
                        val otherUser = it.getValue(User::class.java)
                        FCMSender.pushNotification(
                            context = context,
                            token = otherUser!!.cloudToken,
                            title = currentUser.username,
                            message = msg.text,
                            from = group.id,
                            to = otherUser.uid,
                            action = NotificationService.ACTION_NOTIFICATION
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

    fun addOrUpdatePhoto(fileInBytes: ByteArray, group: Group, groupReference: DatabaseReference, onSuccess: ((Uri) -> Unit)? = null) {
        val photoNodeRef = FirebaseStorage
                                .getInstance()
                                .getReference("group_photos")
                                .child(group.id)

        Thread {
            photoNodeRef.putBytes(fileInBytes).addOnSuccessListener {
                Thread {
                    photoNodeRef.downloadUrl.addOnSuccessListener {
                        group.groupPhoto = it.toString()
                        groupReference.setValue(group)
                        onSuccess?.invoke(it)
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