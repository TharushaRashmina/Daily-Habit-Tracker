package com.example.dailyhabittracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyhabittracker.ui.HomeFragment
import com.example.dailyhabittracker.ui.HabitsFragment
import com.example.dailyhabittracker.ui.MoodFragment
import com.example.dailyhabittracker.ui.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // BottomNavigation setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when(item.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_habits -> HabitsFragment()
                R.id.menu_mood -> MoodFragment()
                else -> SettingsFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
            true
        }

        // Select default fragment - now home instead of habits
        if (savedInstanceState == null) bottomNav.selectedItemId = R.id.menu_home
    }
}