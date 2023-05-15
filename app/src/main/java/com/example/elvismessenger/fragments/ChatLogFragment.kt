package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.ChatLogAdapter
import com.example.elvismessenger.db.ChatRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso

class ChatLogFragment : Fragment(R.layout.fragment_chat_log) {

    data class ChatMessage(val currentUserUID: String = "",
                           val otherUserUID: String = "",
                           val text: String = "",
                           val time: Long = 0)

    companion object {
        const val ANOTHER_USER = "another_user"
    }

    private lateinit var otherUser: User
    private lateinit var currentUser: User
    private lateinit var chatQuery: Query

    private lateinit var anotherUserPhoto: ImageView
    private lateinit var anotherUsername: TextView
    private lateinit var anotherUserState: TextView

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anotherUserPhoto = view.findViewById(R.id.user_photo)
        anotherUsername = view.findViewById(R.id.username)
        anotherUserState = view.findViewById(R.id.current_state)

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
                    Picasso.get().load(R.drawable.dornan).into(anotherUserPhoto)
                }
                anotherUsername.text = user.username
            }
        }

        UserRepository.currentUser?.let {
            it.value?.let { user ->
                currentUser = user
            }
        }

        chatQuery = ChatRepository.getInstance().getChat(ChatRepository.getChatID(currentUser.uid, otherUser.uid))

        // Часть кода для работы списка чатов
        val recyclerView: RecyclerView = view.findViewById(R.id.list_recycler_view_chat_log)
        val layoutManager = LinearLayoutManager(context)
        // Пердаем layout в наш recycleView
        recyclerView.layoutManager = layoutManager

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(chatQuery, ChatMessage::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = ChatLogAdapter(options, otherUser) {
            Toast.makeText(requireContext(), "You clicked on ${otherUser.username}", Toast.LENGTH_SHORT).show()
        }
        // Передаем адаптер
        recyclerView.adapter = adapter

        // Для отправки сообщения локально
        val sendButton: Button = view.findViewById(R.id.send_button_chat_log)
        val inputText: EditText = view.findViewById(R.id.input_edit_text_chat_log)

        sendButton.setOnClickListener {
            val msg = ChatMessage(currentUser.uid, otherUser.uid,  inputText.text.toString(), System.currentTimeMillis())
            chatQuery.ref.push().setValue(msg) { error, _ ->
                error?.let {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
                inputText.text.clear()
            }
        }
    }
}