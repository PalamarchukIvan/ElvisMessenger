package com.example.elvismessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elvismessenger.R
import com.example.elvismessenger.fragments.ChatListFragment
import com.example.elvismessenger.fragments.settings.LanguageSettingsFragment
import org.w3c.dom.Text

class LanguageOptionsAdapter(private val languages: Array<String>) :  RecyclerView.Adapter<LanguageOptionsAdapter.LangOptionViewHolder>() {

    var onLangClick: ((String) -> Unit)? = null

    companion object {
        private const val EVEN_LANG = 0
        private const val ODD_LANG = 1
    }

    class LangOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val langName: TextView = itemView.findViewById(R.id.lang_name)

        fun bind(lang: String) {
            langName.text = lang
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 2) {
            0 -> EVEN_LANG
            else -> ODD_LANG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangOptionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            EVEN_LANG -> LangOptionViewHolder(layoutInflater.inflate(R.layout.lang_item_selected, parent, false))
            ODD_LANG -> LangOptionViewHolder(layoutInflater.inflate(R.layout.lang_item, parent, false))
            else -> LangOptionViewHolder(layoutInflater.inflate(R.layout.chats_item_even, parent, false))
        }
    }

    override fun getItemCount(): Int = languages.size

    override fun onBindViewHolder(holder: LangOptionViewHolder, position: Int) {
        val lang = languages[position]

        holder.bind(lang)

        holder.itemView.setOnClickListener {
            onLangClick?.invoke(lang)
        }
    }
}