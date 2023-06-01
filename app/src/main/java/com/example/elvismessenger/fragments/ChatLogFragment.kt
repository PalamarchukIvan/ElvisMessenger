package com.example.elvismessenger.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatLogAdapter
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.FCMSender
import com.example.elvismessenger.utils.LinearLayoutManagerWrapper
import com.example.elvismessenger.utils.NotificationService
import com.example.elvismessenger.utils.StorageUtil
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ChatLogFragment : Fragment(R.layout.fragment_chat_log) {

    companion object {
        const val ANOTHER_USER = "another_user"
        const val RC_SELECT_IMG = 101
    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var otherUser: User
    private lateinit var currentUser: User
    private lateinit var chatQuery: Query

    private lateinit var anotherUserPhoto: ImageView
    private lateinit var anotherUsername: TextView
    private lateinit var anotherUserState: TextView
    private lateinit var returnBtn: ImageView

    private lateinit var deleteFAB: FloatingActionButton
    private lateinit var sendImageBtn: FloatingActionButton
    private lateinit var cancelDeleteBtn: ImageView

    private lateinit var adapter: ChatLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        super.onDestroyView()
    }

    override fun onResume() {
        recyclerView.smoothScrollToPosition(adapter.itemCount)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anotherUserPhoto = view.findViewById(R.id.user_photo)
        anotherUsername = view.findViewById(R.id.username)
        anotherUserState = view.findViewById(R.id.current_state)
        returnBtn = view.findViewById(R.id.return_btn)

        deleteFAB = view.findViewById(R.id.delete_msg_btn)
        sendImageBtn = view.findViewById(R.id.send_image_btn)
        cancelDeleteBtn = view.findViewById(R.id.cancel_delete_btn)
        returnBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        // Клик на профиль другого юзера
        val otherUserProfile = view.findViewById<LinearLayout>(R.id.other_user_profile)

        otherUserProfile.setOnClickListener {
            val args = Bundle()
            args.putParcelable("otherUser", otherUser)
            Navigation.findNavController(view)
                .navigate(R.id.action_chatLogFragment_to_otherUserProfile, args)
        }

        // Отмена удаления сообщений
        cancelDeleteBtn.setOnClickListener {
            adapter.uncheckItems()
            cancelDeleteBtn.visibility = View.INVISIBLE
            showDeleteFab(View.INVISIBLE)
        }

        // Нажатие на удаление сообщений
        deleteFAB.setOnClickListener {
            showDeleteFab(View.INVISIBLE)
            cancelDeleteBtn.visibility = View.INVISIBLE
            adapter.delete()
        }

        arguments?.let {
            it.getParcelable<User>(ANOTHER_USER)?.let { user ->
                otherUser = user

                if (user.photo != "") {
                    anotherUserPhoto.let { photo ->
                        Picasso.get()
                            .load(user.photo)
                            .into(photo)
                    }
                } else {
                    Picasso.get().load(R.drawable.no_pfp).into(anotherUserPhoto)
                }
                anotherUsername.text = user.username
            }
        }

        UserRepository.currentUser.let {
            it.value?.let { user ->
                currentUser = user
            }
        }

        UserRepository.getInstance().getUserByUID(otherUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    anotherUserState.text = if (user!!.isActive) {
                        "Online"
                    } else {
                        "Last seen ${TimeAgo.using(user.lastSeen)}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        chatQuery = ChatRepository.getInstance()
            .getChat(ChatRepository.getChatID(currentUser.uid, otherUser.uid))

        // Часть кода для работы списка чатов
        recyclerView = view.findViewById(R.id.list_recycler_view_chat_log)
        val layoutManager = LinearLayoutManagerWrapper(context)
        // Пердаем layout в наш recycleView
        recyclerView.layoutManager = layoutManager

        chatQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerView.smoothScrollToPosition((snapshot.childrenCount).toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(chatQuery, ChatMessage::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ChatLogAdapter(
            options,
            otherUser,
            onLongItemClick = { state -> showDeleteFab(state) }
        )

        // Передаем адаптер
        recyclerView.adapter = adapter

        // Для отправки сообщения локально
        val sendButton: Button = view.findViewById(R.id.send_button_chat_log)
        val inputText: EditText = view.findViewById(R.id.input_edit_text_chat_log)

        inputText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                FCMSender.pushNotification(
                    requireContext(),
                    otherUser.cloudToken,
                    from = currentUser.uid,
                    to = otherUser.uid,
                    action = NotificationService.ACTION_IS_WRITING
                )
            } else {
                FCMSender.pushNotification(
                    requireContext(),
                    otherUser.cloudToken,
                    from = currentUser.uid,
                    to = otherUser.uid,
                    action = NotificationService.ACTION_IS_NOT_WRITING
                )
            }
        }

        inputText.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                inputText.requestFocus()
            } else {
                inputText.clearFocus()
            }
        }

        // Нажатие на отправку сообщения
        sendButton.setOnClickListener {
            isUserBanned(currentUser, otherUser) { isBanned ->
                if (inputText.text.isNotEmpty()) {
                    if (!isBanned) {
                        // Для отправки сообщения
                        val msg = ChatMessage(
                            currentUser.uid,
                            inputText.text.toString(),
                            img = "",
                            System.currentTimeMillis()
                        )

                        ChatRepository.getInstance()
                            .sendMessage(msg, currentUser, otherUser, chatQuery, requireContext()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${it?.message.toString()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        inputText.text.clear()
                        recyclerView.smoothScrollToPosition(adapter.itemCount)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "${otherUser.username} banned you",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Нажатие на отправку картинки
        sendImageBtn.setOnClickListener {
            isUserBanned(currentUser, otherUser) { isBanned ->
                if (!isBanned) {
                    val iGallery = Intent(Intent.ACTION_PICK)
                    iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    startActivityForResult(iGallery, RC_SELECT_IMG)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "${otherUser.username} banned you",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun isMessagingTo(to: String, from: String): Boolean {
        return (to == currentUser.uid && from == otherUser.uid) || (to == otherUser.uid && from == currentUser.uid)
    }

    // Для выполнения отправки картинки
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SELECT_IMG) {
                val selectedImagePath = data?.data

                val selectedImageBmp =
                    MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImagePath)

                val compressedImg = StorageUtil.compressImg(selectedImageBmp)

                if (compressedImg != null) {
                    StorageUtil.uploadMsgImg(compressedImg) { imagePath ->
                        val msg = ChatMessage(
                            currentUser.uid,
                            img = imagePath,
                            time = System.currentTimeMillis()
                        )

                        ChatRepository.getInstance()
                            .sendMessage(msg, currentUser, otherUser, chatQuery, requireContext()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${it?.message.toString()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Size of the image should be less than 8 mb",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        recyclerView.recycledViewPool.clear()
        recyclerView.scrollToPosition(adapter.itemCount)
        adapter.notifyDataSetChanged()
        adapter.startListening()
    }

    private fun isUserBanned(currentUser: User, otherUser: User, onSuccess: (Boolean) -> Unit) {
        val otherUserBannedListRef =
            FirebaseDatabase.getInstance().getReference("/users/${otherUser.uid}/bannedUsers")

        otherUserBannedListRef.get().addOnSuccessListener { snapshot ->
            for (i in snapshot.children) {
                if (currentUser.uid == i.key) {
                    onSuccess(true)
                    return@addOnSuccessListener
                }
            }

            onSuccess(false)
        }
    }

    fun makeOtherUserIsWriting() {
        anotherUserState.text = "Is writing..."
    }

    fun makeOtherUserIsNotWriting() {
        anotherUserState.text = "Online"
    }

    private fun showDeleteFab(state: Int) {
        cancelDeleteBtn.visibility = state
        deleteFAB.visibility = state
    }
}