package com.example.elvismessenger.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.CreateGroupAdapter
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.User
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.EditProfileFragment
import com.example.elvismessenger.utils.LinearLayoutManagerWrapper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.launch

class CreateGroupFragment : Fragment(R.layout.fragment_create_group) {

    private lateinit var confirmBtn: FloatingActionButton
    private lateinit var groupNameET: EditText
    private lateinit var groupImage: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: CreateGroupAdapter

    private var userList: MutableList<User> = mutableListOf()
    private val chosenUsers: MutableList<User> = mutableListOf(UserRepository.currentUser.value!!)
    private var photo: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmBtn = view.findViewById(R.id.confirm_group_creation)
        groupNameET = view.findViewById(R.id.group_name)
        groupImage = view.findViewById(R.id.selected_group_image)
        progressBar = view.findViewById(R.id.progress)


        val recyclerView = view.findViewById<RecyclerView>(R.id.user_list)
        recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext())

        adapter = CreateGroupAdapter(userList) {
            return@CreateGroupAdapter if (!chosenUsers.contains(it)) {
                chosenUsers.add(it)
                true
            } else {
                chosenUsers.remove(it)
                false
            }
        }

        groupImage.setOnClickListener {
            val iGallery = Intent(Intent.ACTION_PICK)
            iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(iGallery, EditProfileFragment.NEW_PHOTO_REQ_CODE)
        }

        // Для нахождения юзеров локально
        val inputText: EditText = view.findViewById(R.id.search_user)
        inputText.addTextChangedListener {
            val newUserList = userList.filter { user ->
                user.username.contains(it.toString())
            }.toMutableList()
            adapter.userToShowList = newUserList
            adapter.notifyDataSetChanged()
        }

        confirmBtn.setOnClickListener {
            if (groupNameET.text.isNotEmpty()) {
                GroupRepository.createGroup(
                    chosenUsers,
                    groupNameET.text.toString(),
                    photo,
                    requireContext()
                )
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Name of the group can't be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        initRecyclerViewContent(recyclerView)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EditProfileFragment.NEW_PHOTO_REQ_CODE) {
                groupImage.setImageURI(data?.data)
                photo = data?.data
            }
        }
    }

    private fun initRecyclerViewContent(recyclerView: RecyclerView) {
        if (userList.size == 0) {
            // Заполнение списка юзеров из репозитория
            val query = UserRepository.getInstance().getAllUsers()
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                query.snapshots.collect {
                    recyclerView.adapter = adapter
                    userList.clear()
                    for (i in it.children) {
                        val user = i.getValue(User::class.java)
                        user.let {
                            if (user?.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                                userList.add(user!!)
                                adapter.notifyItemChanged(0)
                            }
                        }
                    }
                    progressBar.visibility = View.INVISIBLE
                }
            }
        } else {
            recyclerView.adapter = adapter
        }
    }
}