package com.example.dailyhabittracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.databinding.FragmentSettingsBinding
import com.example.dailyhabittracker.utils.AlarmUtils

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PreferencesHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        prefs = PreferencesHelper(requireContext())

        val s = prefs.settings
        binding.switchHydration.isChecked = s.hydrationEnabled
        binding.etInterval.setText(s.hydrationIntervalMinutes.toString())

        binding.btnSaveSettings.setOnClickListener {
            val interval = binding.etInterval.text.toString().toIntOrNull() ?: 120
            val enabled = binding.switchHydration.isChecked
            prefs.settings = prefs.settings.copy(hydrationIntervalMinutes = interval, hydrationEnabled = enabled)
            if (enabled) {
                // request notifications permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
                    } else {
                        AlarmUtils.scheduleHydration(requireContext(), interval)
                    }
                } else {
                    AlarmUtils.scheduleHydration(requireContext(), interval)
                }
            } else {
                AlarmUtils.cancelHydration(requireContext())
            }
        }

        return binding.root
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}