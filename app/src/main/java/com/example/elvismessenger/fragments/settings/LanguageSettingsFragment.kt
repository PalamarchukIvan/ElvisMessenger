package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.adapters.ChatsListAdapter
import com.example.elvismessenger.adapters.LanguageOptionsAdapter
import com.example.elvismessenger.fragments.ChatListFragment

class LanguageSettingsFragment : Fragment(R.layout.fragment_language_settings) {

    private lateinit var selectedLang: TextView

    companion object {
        val LANGUAGE_LIST = arrayOf(
            "English",
            "Ukrainian",
            "Russian",
            "Polish",
            "German",
            "Spanish",
            "Belarusian",
            "Chinese",
            "Japanese",
            "French",
            "Spanish",
            "Slovak",
            "Czech",
            "Hungarian",
            "Romanian",
            "Lithuanian"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedLang = view.findViewById(R.id.selected_lang)
        selectedLang.setText(MainActivity.sp.getString(SettingsFragment.LANGUAGE_SELECTED, "test lang"))

        val recyclerView: RecyclerView = view.findViewById(R.id.language_recycler_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val langAdapter = LanguageOptionsAdapter(LANGUAGE_LIST)

        langAdapter.onLangClick = {
            selectedLang.text = it
            saveData()
        }

        recyclerView.adapter = langAdapter
    }

    private fun saveData() {
        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.LANGUAGE_SELECTED, selectedLang.text.toString())
        editor?.apply()
    }
}