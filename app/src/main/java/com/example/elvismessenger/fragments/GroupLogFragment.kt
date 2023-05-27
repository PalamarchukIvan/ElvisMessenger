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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.GroupLogAdapter
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.LinearLayoutManagerWrapper
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class GroupLogFragment: Fragment(R.layout.fragment_group_log) {
    companion object {
        const val ANOTHER_GROUP = "another_group"
    }


    private lateinit var recyclerView: RecyclerView

    private lateinit var currentGroup: Group
    private lateinit var currentUser: User
    private lateinit var groupQuery: Query

    private lateinit var groupPhoto: ImageView
    private lateinit var groupName: TextView
    private lateinit var groupState: TextView
    private lateinit var returnBtn: ImageView

    private lateinit var deleteFAB: FloatingActionButton
    private lateinit var cancelDeleteBtn: ImageView

    private lateinit var adapter: GroupLogAdapter

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

        groupPhoto = view.findViewById(R.id.user_photo)
        groupName = view.findViewById(R.id.username)
        groupState = view.findViewById(R.id.current_state)
        returnBtn = view.findViewById(R.id.return_btn)

        deleteFAB = view.findViewById(R.id.delete_msg_btn)
        cancelDeleteBtn = view.findViewById(R.id.cancel_delete_btn)
        returnBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        // Отмена удаления сообщений
        cancelDeleteBtn.setOnClickListener {
            cancelDeleteBtn.visibility = View.INVISIBLE
            showDeleteFab(View.INVISIBLE)
        }

        // Нажатие на удаление сообщений
        deleteFAB.setOnClickListener {
            showDeleteFab(View.INVISIBLE)
            cancelDeleteBtn.visibility = View.INVISIBLE
        }

        arguments?.let {
            it.getParcelable<Group>(ANOTHER_GROUP)?.let { group_ ->
                currentGroup = group_

                if (currentGroup.groupPhoto != "") {
                    groupPhoto.let { photo ->
                        Picasso.get()
                            .load(currentGroup.groupPhoto)
                            .into(photo)
                    }
                } else {
                    Picasso.get().load(R.drawable.dornan).into(groupPhoto)
                }
                groupName.text = currentGroup.groupName
            }
        }

        UserRepository.currentUser.let {
            it.value?.let { user ->
                currentUser = user
            }
        }

        groupState.text = "${currentGroup.userList.size} users"

        groupQuery = GroupRepository.getGroupMessages(currentGroup.id)

        // Часть кода для работы списка чатов
        recyclerView = view.findViewById(R.id.list_recycler_view_group_log)
        val layoutManager = LinearLayoutManagerWrapper(context)
        // Пердаем layout в наш recycleView
        recyclerView.layoutManager = layoutManager

        groupQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerView.smoothScrollToPosition((snapshot.childrenCount).toInt())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(groupQuery, ChatMessage::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = GroupLogAdapter(
            options,
            currentGroup,
            {
                Toast.makeText(
                    requireContext(),
                    "You clicked on ${currentGroup.groupName}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onLongItemClick = { state -> showDeleteFab(state) })

        // Передаем адаптер
        recyclerView.adapter = adapter

        // Для отправки сообщения локально
        val sendButton: Button = view.findViewById(R.id.send_button_group_log)
        val inputText: EditText = view.findViewById(R.id.input_edit_text_group_log)

        inputText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                // TODO: Cделать нотификация, что кто-то пишет
//                FCMSender.pushNotification(requireContext(),
//                    currentUser.cloudToken,
//                    from = currentUser.uid,
//                    to = currentUser.uid,
//                    action = NotificationService.ACTION_IS_WRITING)
            } else {
//                FCMSender.pushNotification(requireContext(),
//                    currentUser.cloudToken,
//                    from = currentUser.uid,
//                    to = currentUser.uid,
//                    action = NotificationService.ACTION_IS_NOT_WRITING)
            }
        }

        inputText.addTextChangedListener {
            if(!it.isNullOrBlank()) {
                inputText.requestFocus()
            } else {
                inputText.clearFocus()
            }
        }

        sendButton.setOnClickListener {
            // Для отправки сообщения
            val msg = ChatMessage(
                currentUser.uid,
                inputText.text.toString(),
                System.currentTimeMillis()
            )
            GroupRepository.sendMessage(msg, currentUser, currentGroup, groupQuery, requireContext()) {
                Toast.makeText(requireContext(), "Что-то пошло не так, ибо я даун ${it.toString()}", Toast.LENGTH_SHORT).show()
            }

            inputText.text.clear()
            recyclerView.smoothScrollToPosition(adapter.itemCount)
        }
    }

    private fun showDeleteFab(state: Int) {
        cancelDeleteBtn.visibility = state
        deleteFAB.visibility = state
    }
}