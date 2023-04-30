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
import com.example.elvismessenger.utils.UserPersonalSettings

class NotificationsSettingsFragment : Fragment() {

    private lateinit var newNotificationVolume: SeekBar
    private lateinit var currentNotificationVolume: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        newNotificationVolume = view.findViewById(R.id.new_notifications_volume)
        currentNotificationVolume = view.findViewById(R.id.current_notifications_volume)

        newNotificationVolume.progress = UserPersonalSettings.livaDataInstance.value!!.notificationVolume
        currentNotificationVolume.text = newNotificationVolume.progress.toString()

        newNotificationVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentNotificationVolume.text = newNotificationVolume.progress.toString()
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
        editor.putInt(SettingsFragment.NOTIFICATION_VOLUME, newNotificationVolume.progress)
        editor.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_settings, container, false)
    }
}