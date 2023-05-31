package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.GroupProfileAdapter
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.example.elvismessenger.db.UserRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GroupProfileFragment : Fragment(R.layout.fragment_group_profile) {

    companion object {
        const val GROUP_DATA = "group_data"
    }

    private lateinit var groupPhoto: CircleImageView
    private lateinit var groupName: EditText
    private lateinit var addToGroup: LinearLayout
    private lateinit var leaveFromGroup: LinearLayout
    private lateinit var memberCountTV: TextView

    private lateinit var memberRecyclerView: RecyclerView
    private val groupLiveData: MutableLiveData<Group> = MutableLiveData()
    private lateinit var adapter: GroupProfileAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            it.getParcelable<Group>(GROUP_DATA)?.let { bundleGroup ->
                groupLiveData.value = bundleGroup
                groupLiveData.postValue(bundleGroup)
            }
        }

        initUiElements(view)

        GroupRepository.getGroupById(groupLiveData.value!!.id).addValueEventListener(object : ValueEventListener {
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

        memberRecyclerView = view.findViewById(R.id.group_members_list)

        groupLiveData.observe(viewLifecycleOwner) {group ->
            groupName.setText(group.groupName)
            groupPhoto.let {
                if(group.groupPhoto.isNotEmpty()) {
                    Picasso
                        .get()
                        .load(group.groupPhoto)
                        .into(it)
                } else {
                    Picasso
                        .get()
                        .load(R.drawable.dornan)
                        .into(it)
                }
            }
            memberCountTV.text = "${group.userList.size} members"
        }

        addToGroup.setOnClickListener {

        }

        leaveFromGroup.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Leaving")
                .setMessage("Are you sure that you want to leave group?")
                .setNegativeButton("no", null)
                .setPositiveButton("yes") { _, _ ->
                    GroupRepository.deleteUserFromGroup(UserRepository.currentUser.value!!.uid, groupLiveData.value!!.id)
                    requireActivity().onBackPressed()
                    requireActivity().onBackPressed()
                }
                .show()
        }

        groupPhoto.setOnClickListener {

        }
    }
}