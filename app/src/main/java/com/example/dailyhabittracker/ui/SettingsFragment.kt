package com.example.dailyhabittracker.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.utils.AlarmUtils
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

class SettingsFragment : Fragment() {
    private lateinit var prefs: PreferencesHelper
    private lateinit var switchHydration: SwitchMaterial
    private lateinit var etInterval: TextInputEditText
    private lateinit var btnSaveSettings: Button
    private lateinit var btnAddWater: Button
    private lateinit var tvWaterProgress: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize views
        switchHydration = view.findViewById(R.id.switchHydration)
        etInterval = view.findViewById(R.id.etInterval)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
        btnAddWater = view.findViewById(R.id.btnAddWater)
        tvWaterProgress = view.findViewById(R.id.tvWaterProgress)

        prefs = PreferencesHelper(requireContext())

        // Load current settings
        val s = prefs.settings
        switchHydration.isChecked = s.hydrationEnabled
        etInterval.setText(s.hydrationIntervalMinutes.toString())

        // Update water progress display
        updateWaterProgress()

        // Save settings button
        btnSaveSettings.setOnClickListener {
            val interval = etInterval.text.toString().toIntOrNull() ?: 120
            val enabled = switchHydration.isChecked
            prefs.settings = prefs.settings.copy(hydrationIntervalMinutes = interval, hydrationEnabled = enabled)

            if (enabled) {
                // Request notifications permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
                        return@setOnClickListener
                    }
                }

                // Check for exact alarm permission on Android 12+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("⚠️ Permission Required")
                            .setMessage("For precise hydration reminders, this app needs permission to schedule exact alarms. Please enable it in the system settings.")
                            .setPositiveButton("Open Settings") { _, _ ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    startActivity(Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                                }
                            }
                            .setNegativeButton("Use Approximate Timing", null)
                            .show()
                    }
                }

                AlarmUtils.scheduleHydration(requireContext(), interval)

                // Show test notification button
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("✅ Settings Saved")
                    .setMessage("Hydration reminders are now enabled!\n\nNext reminder in $interval minutes.\n\nWant to test the notification now?")
                    .setPositiveButton("Test Now") { _, _ ->
                        // Show a test notification immediately
                        showTestNotification()
                    }
                    .setNegativeButton("OK", null)
                    .show()

            } else {
                AlarmUtils.cancelHydration(requireContext())
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Settings Saved")
                    .setMessage("Hydration reminders have been disabled.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        // Add water button
        btnAddWater.setOnClickListener {
            val currentCount = prefs.getDailyWaterCount()
            val maxGlasses = 8

            if (currentCount >= maxGlasses) {
                // Show message when limit is reached
                val snackbar = com.google.android.material.snackbar.Snackbar.make(
                    view, "🎉 Daily goal achieved! You've had $maxGlasses glasses today!",
                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                )
                snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_green_light, null))
                snackbar.show()
            } else {
                prefs.incrementDailyWaterCount()
                updateWaterProgress()

                val newCount = prefs.getDailyWaterCount()

                // Show different messages based on progress
                val message = when {
                    newCount >= maxGlasses -> "🎉 Congratulations! Daily goal achieved! 💧"
                    newCount >= maxGlasses - 1 -> "🎯 One more glass to reach your goal! 💧"
                    else -> "Water logged! Keep it up! 💧"
                }

                val snackbar = com.google.android.material.snackbar.Snackbar.make(
                    view, message,
                    com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                )
                snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_blue_light, null))
                snackbar.show()
            }
        }

        return view
    }

    private fun updateWaterProgress() {
        val waterCount = prefs.getDailyWaterCount()
        val targetGlasses = 8
        tvWaterProgress.text = "$waterCount of $targetGlasses glasses"
    }

    private fun showTestNotification() {
        val hydrationReceiver = com.example.dailyhabittracker.receivers.HydrationReceiver()
        hydrationReceiver.onReceive(requireContext(), Intent())

        com.google.android.material.snackbar.Snackbar.make(
            requireView(),
            "Test notification sent! Check your notification panel 📱",
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        updateWaterProgress() // Refresh water count when returning to settings
    }
}