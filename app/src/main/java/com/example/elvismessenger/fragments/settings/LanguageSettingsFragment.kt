package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity
import com.example.elvismessenger.adapters.LanguageOptionsAdapter

class LanguageSettingsFragment : Fragment(R.layout.fragment_language_settings) {

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

        val recyclerView: RecyclerView = view.findViewById(R.id.language_recycler_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Добавление линии между элементами чата
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val langAdapter = LanguageOptionsAdapter(LANGUAGE_LIST)

        langAdapter.onLangClick = {
            saveData(it)
        }

        recyclerView.adapter = langAdapter
    }

    private fun saveData(language: String) {
        val editor =
            MainActivity.sp.edit()
        editor?.putString(SettingsFragment.LANGUAGE_SELECTED, language)
        editor?.apply()
    }
}