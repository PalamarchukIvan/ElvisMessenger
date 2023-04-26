package com.example.elvismessenger.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.elvismessenger.R
import com.example.elvismessenger.activities.MainActivity

class ChatSettingsFragment : Fragment() {

    private lateinit var newTextSize: SeekBar
    private lateinit var currentTextSize: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTextSize = view.findViewById(R.id.new_text_size)
        currentTextSize = view.findViewById(R.id.current_text_size)

        newTextSize.progress = MainActivity.sp.getInt(SettingsFragment.TEXT_SIZE, 18)
        currentTextSize.text = newTextSize.progress.toString()

        newTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentTextSize.text = newTextSize.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                saveData()
            }
        })
    }

    private fun saveData() {
        val editor =
            MainActivity.sp.edit()
        editor.putInt(SettingsFragment.TEXT_SIZE, newTextSize.progress)
        editor.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_settings, container, false)
    }

}