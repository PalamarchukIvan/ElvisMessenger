package com.example.elvismessenger.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.GroupProfileAdapter
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.UserRepository
import com.example.elvismessenger.fragments.settings.EditProfileFragment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class GroupProfileFragment : Fragment(R.layout.fragment_group_profile) {

    companion object {
        const val GROUP_DATA = "group_data"
    }

    private lateinit var groupPhoto: CircleImageView
    private lateinit var groupName: EditText
    private lateinit var addToGroup: LinearLayout
    private lateinit var leaveFromGroup: LinearLayout
    private lateinit var memberCountTV: TextView
    private lateinit var confirmButton: AppCompatButton

    private lateinit var memberRecyclerView: RecyclerView
    private val groupLiveData: MutableLiveData<Group> = MutableLiveData()
    private lateinit var adapter: GroupProfileAdapter
    private var newPhotoBytes: ByteArray? = null


    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            it.getParcelable<Group>(GROUP_DATA)?.let { bundleGroup ->
                groupLiveData.value = bundleGroup
                groupLiveData.postValue(bundleGroup)
            }
        }

        initUiElements(view)
        setFragmentResultListener(ChooseWhoToAddFragment.RESULT_LIST_CODE) { key, bundle ->
            val newUsers = bundle.getStringArrayList(ChooseWhoToAddFragment.RESULT_LIST_DATA)!!
            GroupRepository.addNewUsers(groupLiveData.value!!, newUsers)
        }

        GroupRepository.getGroupById(groupLiveData.value!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupLiveData.postValue(snapshot.getValue(Group::class.java)!!)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        val memberQuery = GroupRepository.getGroupUsers(groupLiveData.value!!.id)
        val option = FirebaseRecyclerOptions.Builder<String>()
            .setQuery(memberQuery, String::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = GroupProfileAdapter(option) {

        }

        memberRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        memberRecyclerView.adapter = adapter
    }

    private fun initUiElements(view: View) {
        groupPhoto = view.findViewById(R.id.group_pfp_image)
        groupName = view.findViewById(R.id.group_name)
        addToGroup = view.findViewById(R.id.add_to_group)
        leaveFromGroup = view.findViewById(R.id.leave_from_group)
        memberCountTV = view.findViewById(R.id.group_member_count)
        confirmButton = view.findViewById(R.id.group_profile_button)

        memberRecyclerView = view.findViewById(R.id.group_members_list)

        groupLiveData.observe(viewLifecycleOwner) { group ->
            groupName.setText(group.groupName)
            groupPhoto.let {
                if (group.groupPhoto.isNotEmpty()) {
                    Picasso
                        .get()
                        .load(group.groupPhoto)
                        .into(it)
                } else {
                    Picasso
                        .get()
                        .load(R.drawable.no_pfp)
                        .into(it)
                }
            }
            memberCountTV.text = "${group.userList.size} members"
        }

        addToGroup.setOnClickListener {
            val args = Bundle()
            args.putBoolean("12", true)
            args.putParcelable(ChooseWhoToAddFragment.USER_WHO_ADD_MEMBERS, UserRepository.currentUser.value)
            args.putParcelable(ChooseWhoToAddFragment.GROUP_TO_ADD_MEMBERS, groupLiveData.value)
            Navigation.findNavController(view).navigate(R.id.action_groupProfileFragment_to_chooseWhoToAddFragment, args)
        }

        leaveFromGroup.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Leaving")
                .setMessage("Are you sure that you want to leave group?")
                .setNegativeButton("no", null)
                .setPositiveButton("yes") { _, _ ->
                    GroupRepository.deleteUserFromGroup(
                        UserRepository.currentUser.value!!.uid,
                        groupLiveData.value!!.id
                    )
                    requireActivity().onBackPressed()
                    requireActivity().onBackPressed()
                }
                .show()
        }

        groupName.addTextChangedListener {
            if (it.toString() != groupLiveData.value!!.groupName.toString()) {
                confirmButton.visibility = View.VISIBLE
            }
        }

        groupPhoto.setOnClickListener {
            val iGallery = Intent(Intent.ACTION_PICK)
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iGallery, EditProfileFragment.NEW_PHOTO_REQ_CODE)
            confirmButton.visibility = View.VISIBLE
        }

        confirmButton.setOnClickListener { _ ->
            newPhotoBytes?.let {
                GroupRepository.addOrUpdatePhoto(
                    it,
                    groupLiveData.value!!,
                    GroupRepository.getGroupById(groupLiveData.value!!.id)
                ) { _ ->
                    newPhotoBytes = null
                }
            }
            GroupRepository.updateGroupName(groupLiveData.value!!.id, groupName.text.toString())

            confirmButton.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EditProfileFragment.NEW_PHOTO_REQ_CODE) {

                data?.data?.let {//Сжатие
                    val bmp = MediaStore.Images.Media.getBitmap(context?.contentResolver, it);
                    val baos = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                    newPhotoBytes = baos.toByteArray()
                    Picasso
                        .get()
                        .load(it)
                        .into(groupPhoto)

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        memberRecyclerView.recycledViewPool.clear()
        adapter.notifyDataSetChanged()
        adapter.startListening()
    }
}