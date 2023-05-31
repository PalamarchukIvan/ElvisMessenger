package com.example.elvismessenger.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.GroupLogAdapter
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.utils.LinearLayoutManagerWrapper
import com.example.elvismessenger.utils.StorageUtil
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlin.streams.toList

class GroupLogFragment: Fragment(R.layout.fragment_chat_log) {
    companion object {
        const val ANOTHER_GROUP = "another_group"
    }


    private lateinit var recyclerView: RecyclerView

    private lateinit var currentGroup: Group
    private lateinit var currentUser: User
    private val userList: MutableList<User> = mutableListOf()

    private lateinit var groupQuery: Query

    private lateinit var groupPhoto: ImageView
    private lateinit var groupName: TextView
    private lateinit var groupState: TextView
    private lateinit var returnBtn: ImageView

    private lateinit var deleteFAB: FloatingActionButton
    private lateinit var sendImageBtn: FloatingActionButton
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
        recyclerView.scrollTo(0, recyclerView.bottom)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupPhoto = view.findViewById(R.id.user_photo)
        groupName = view.findViewById(R.id.username)
        groupState = view.findViewById(R.id.current_state)
        returnBtn = view.findViewById(R.id.return_btn)

        deleteFAB = view.findViewById(R.id.delete_msg_btn)
        sendImageBtn = view.findViewById(R.id.send_image_btn)
        cancelDeleteBtn = view.findViewById(R.id.cancel_delete_btn)
        returnBtn.setOnClickListener {
            activity?.onBackPressed()
        }


        groupPhoto.setOnClickListener {
            val args = Bundle()

            args.putParcelable(GroupProfileFragment.GROUP_DATA, currentGroup)
            Navigation.findNavController(view).navigate(R.id.action_groupLogFragment_to_groupProfileFragment, args)
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
            it.getParcelable<Group>(ANOTHER_GROUP)?.let { group_ ->
                currentGroup = group_

                if (currentGroup.groupPhoto != "") {
                    groupPhoto.let { photo ->
                        Picasso.get()
                            .load(currentGroup.groupPhoto)
                            .placeholder(R.drawable.no_pfp)
                            .into(photo)
                    }
                } else {
                    Picasso.get()
                        .load(R.drawable.no_pfp)
                        .into(groupPhoto)
                }
                groupName.text = currentGroup.groupName
            }
        }

        UserRepository.currentUser.let {
            it.value?.let { user ->
                currentUser = user
            }
        }

        GroupRepository.getGroupUsers(currentGroup.id).get().addOnSuccessListener {
            for (i in it.children) {
                UserRepository.getInstance().getUserByUID(i.getValue(String::class.java)!!).get().addOnSuccessListener {
                    userList.add(it.getValue(User::class.java)!!)
                }
            }
        }

        groupState.text = "${currentGroup.userList.size} users"

        groupQuery = GroupRepository.getGroupMessages(currentGroup.id)

        GroupRepository.getGroupById(currentGroup.id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentGroup = snapshot.getValue<Group>()!!
                currentGroup.groupPhoto.let {
                    if(it.isNotEmpty()) {
                        Picasso.get().load(it).into(groupPhoto)
                    } else {
                        Picasso.get().load(R.drawable.no_pfp).into(groupPhoto)
                    }
                }
                groupName.text = currentGroup.groupName
                if(currentGroup.whoAreWriting.size == 0) {
                    groupState.text = "${currentGroup.userList.size} users"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        // Часть кода для работы списка чатов
        recyclerView = view.findViewById(R.id.list_recycler_view_chat_log)
        val layoutManager = LinearLayoutManagerWrapper(context)
        // Пердаем layout в наш recycleView
        recyclerView.layoutManager = layoutManager

        subscribeForNewUsersWhoAreWriting()

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
            onLongItemClick = { state -> showDeleteFab(state) })

        // Передаем адаптер
        recyclerView.adapter = adapter

        // Для отправки сообщения локально
        val sendButton: Button = view.findViewById(R.id.send_button_chat_log)
        val inputText: EditText = view.findViewById(R.id.input_edit_text_chat_log)

        // TODO: Cделать нотификация, что кто-то пишет
        inputText.addTextChangedListener {
            if(!it.isNullOrBlank()) {
                if(!currentGroup.whoAreWriting.contains(currentUser.username)) {
                    GroupRepository.updateWhoIsWriting(true, currentUser.username, currentGroup)
                }
                inputText.requestFocus()
            } else {
                inputText.clearFocus()
                GroupRepository.updateWhoIsWriting(false, currentUser.username, currentGroup)
            }
        }

        sendButton.setOnClickListener {
            if (inputText.text.isNotEmpty()) {
                // Для отправки сообщения
                val msg = ChatMessage(
                    currentUser.uid,
                    inputText.text.toString(),
                    "",
                    System.currentTimeMillis()
                )
                GroupRepository.sendMessage(
                    msg,
                    currentUser,
                    currentGroup,
                    groupQuery,
                    requireContext()
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong ${it.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                inputText.text.clear()
                recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
        }

        sendImageBtn.setOnClickListener {
            val iGallery = Intent(Intent.ACTION_PICK)
            iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(iGallery, ChatLogFragment.RC_SELECT_IMG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == ChatLogFragment.RC_SELECT_IMG) {
                val selectedImagePath = data?.data

                val selectedImageBmp = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImagePath)
                val compressedImg = StorageUtil.compressImg(selectedImageBmp)

                if (compressedImg != null) {
                    StorageUtil.uploadMsgImg(compressedImg) { imagePath ->
                        val msg = ChatMessage(
                            currentUser.uid,
                            img = imagePath,
                            time = System.currentTimeMillis()
                        )

                        GroupRepository.sendMessage(
                            msg,
                            currentUser,
                            currentGroup,
                            groupQuery,
                            requireContext()
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${it?.message.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Size of the image should be less than 8 mb", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun subscribeForNewUsersWhoAreWriting() {
        GroupRepository.getGroupWhoAreWriting(currentGroup.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentGroup.whoAreWriting.clear()
                    for (i in snapshot.children) {
                        currentGroup.whoAreWriting.add(i.getValue(String::class.java)!!)
                    }
                    showUsersWhoAreWriting()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun onStart() {
        super.onStart()
        recyclerView.recycledViewPool.clear()
        recyclerView.scrollTo(0, recyclerView.bottom)
        adapter.notifyDataSetChanged()
        adapter.startListening()
    }

    private fun showUsersWhoAreWriting() {
        val filteredList = currentGroup.whoAreWriting.stream().filter {
            it != currentUser.username
        }.toList()
        if(filteredList.isEmpty()) {
            groupState.text = "${currentGroup.userList.size} users"
        } else if(filteredList.size == 1){
            groupState.text = "${filteredList[0]} is writing"
        } else {
            val s: String = filteredList.reduce { s1, s2 ->
                "$s1, $s2"
            }
            groupState.text = "$s are writing"
        }
    }

    private fun showDeleteFab(state: Int) {
        cancelDeleteBtn.visibility = state
        deleteFAB.visibility = state
    }

    override fun onPause() {
        super.onPause()
        GroupRepository.updateWhoIsWriting(false, currentUser.username, currentGroup)
    }

    override fun onDestroy() {
        super.onDestroy()
        GroupRepository.updateWhoIsWriting(false, currentUser.username, currentGroup)
    }

    fun isMessagingTo(to: String, from: String): Boolean {
        return (to == currentUser.uid && from == currentGroup.id) || (to == currentGroup.id && from == currentUser.uid)
    }

}