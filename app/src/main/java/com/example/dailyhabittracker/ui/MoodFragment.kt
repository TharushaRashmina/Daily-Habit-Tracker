package com.example.dailyhabittracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyhabittracker.data.MoodEntry
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.databinding.FragmentMoodBinding
import com.example.dailyhabittracker.ui.adapters.MoodAdapter
import com.example.dailyhabittracker.ui.dialogs.EmojiDialog

class MoodFragment : Fragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PreferencesHelper
    private lateinit var adapter: MoodAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        prefs = PreferencesHelper(requireContext())
        adapter = MoodAdapter(prefs.moods)
        binding.recyclerMoods.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMoods.adapter = adapter

        binding.fabAddMood.setOnClickListener {
            EmojiDialog { emoji ->
                val entry = MoodEntry(System.currentTimeMillis(), emoji, null, System.currentTimeMillis())
                prefs.addMood(entry)
                adapter.updateData(prefs.moods)
            }.show(parentFragmentManager, "Emoji")
        }

        binding.fabShareMood.setOnClickListener {
            val moods = prefs.moods
            if (moods.isNotEmpty()) {
                val last = moods.first()
                val summary = "Today I felt ${last.emoji}"
                val send = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, summary)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(send, "Share mood"))
            }
        }

        return binding.root
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}