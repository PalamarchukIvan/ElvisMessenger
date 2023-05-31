package com.example.elvismessenger.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.adapters.GroupProfileAdapter
import com.example.elvismessenger.db.ChatMessage
import com.example.elvismessenger.db.Group
import com.example.elvismessenger.db.GroupRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
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
    private lateinit var group: Group
    private lateinit var adapter: GroupProfileAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            it.getParcelable<Group>(GROUP_DATA)?.let { bundleGroup ->
                group = bundleGroup
            }
        }

        initUiElements(view)

        val memberQuery = GroupRepository.getGroupUsers(group.id)
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
}