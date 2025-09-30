package com.example.dailyhabittracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.MoodEntry
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.ui.adapters.MoodAdapter
import com.example.dailyhabittracker.ui.dialogs.EmojiDialog
import com.example.dailyhabittracker.utils.MoodHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MoodFragment : Fragment() {
    private lateinit var prefs: PreferencesHelper
    private lateinit var adapter: MoodAdapter
    private lateinit var recyclerMoods: RecyclerView
    private lateinit var fabAddMood: Button
    private lateinit var fabShareMood: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        // Initialize views
        recyclerMoods = view.findViewById(R.id.recyclerMoods)
        fabAddMood = view.findViewById(R.id.fabAddMood)
        fabShareMood = view.findViewById(R.id.fabShareMood)

        prefs = PreferencesHelper(requireContext())
        adapter = MoodAdapter(prefs.moods)
        recyclerMoods.layoutManager = LinearLayoutManager(requireContext())
        recyclerMoods.adapter = adapter

        fabAddMood.setOnClickListener {
            EmojiDialog { emoji ->
                // Get the mood description automatically based on emoji
                val moodDescription = MoodHelper.getMoodDescription(emoji)
                val entry = MoodEntry(
                    id = System.currentTimeMillis(),
                    emoji = emoji,
                    note = moodDescription, // Store the mood description as a note
                    timestamp = System.currentTimeMillis()
                )
                prefs.addMood(entry)
                adapter.updateData(prefs.moods)
            }.show(parentFragmentManager, "Emoji")
        }

        fabShareMood.setOnClickListener {
            val moods = prefs.moods
            if (moods.isNotEmpty()) {
                val latest = moods.first()
                val moodDescription = MoodHelper.getMoodDescription(latest.emoji)
                val summary = "Today I'm feeling $moodDescription ${latest.emoji}"
                val send = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, summary)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(send, "Share mood"))
            }
        }

        return view
    }
}