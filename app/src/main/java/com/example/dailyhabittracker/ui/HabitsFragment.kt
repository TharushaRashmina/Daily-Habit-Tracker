package com.example.dailyhabittracker.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.Habit
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.databinding.FragmentHabitsBinding
import com.example.dailyhabittracker.ui.adapters.HabitsAdapter
import com.example.dailyhabittracker.ui.dialogs.AddEditHabitDialog
import com.example.dailyhabittracker.widgets.HabitWidgetProvider
import org.threeten.bp.LocalDate

class HabitsFragment : Fragment() {
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PreferencesHelper
    private lateinit var adapter: HabitsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        prefs = PreferencesHelper(requireContext())
        adapter = HabitsAdapter(prefs.habits, ::toggleComplete, ::editHabit, ::deleteHabit)
        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = adapter

        binding.fabAddHabit.setOnClickListener {
            AddEditHabitDialog { habit ->
                prefs.saveHabit(habit)
                refresh()
            }.show(parentFragmentManager, "AddHabit")
        }

        updateHeader()
        return binding.root
    }

    private fun toggleComplete(habit: Habit) {
        val today = LocalDate.now().toString()
        habit.isCompletedMap[today] = !(habit.isCompletedMap[today] ?: false)
        prefs.saveHabit(habit)
        refresh()
    }

    private fun editHabit(habit: Habit) {
        AddEditHabitDialog(habit) { updated ->
            prefs.saveHabit(updated)
            refresh()
        }.show(parentFragmentManager, "EditHabit")
    }

    private fun deleteHabit(habit: Habit) {
        prefs.deleteHabit(habit.id)
        refresh()
    }

    private fun refresh() {
        adapter.updateData(prefs.habits)
        updateHeader()
        // notify widget to update (optional)
        HabitWidgetProvider.updateAllWidgets(requireContext())
    }

    private fun updateHeader() {
        val today = LocalDate.now().toString()
        val list = prefs.habits
        if (list.isEmpty()) {
            binding.tvProgress.text = "No habits yet"
            binding.progressBar.progress = 0
        } else {
            val done = list.count { it.isCompletedMap[today] == true }
            val percent = done * 100 / list.size
            binding.tvProgress.text = "$percent% completed today"
            binding.progressBar.progress = percent
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}