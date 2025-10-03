package com.example.dailyhabittracker.ui

import android.os.Bundle
import android.view.*
import android.view.View
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
        // Show confirmation dialog before deleting
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.title}\"?\n\nThis action cannot be undone and will remove all progress data for this habit.")
            .setPositiveButton("Delete") { _, _ ->
                prefs.deleteHabit(habit.id)
                refresh()
            }
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
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
            binding.tvProgressPercentage.text = "0%"
            binding.tvProgressPercentage.visibility = View.VISIBLE
            binding.ivCompletionTick.visibility = View.GONE
        } else {
            val done = list.count { it.isCompletedMap[today] == true }
            val total = list.size
            val percent = done * 100 / total

            binding.tvProgress.text = "$done of $total habits completed today"
            binding.progressBar.progress = percent

            // Show tick icon when all habits completed, otherwise show percentage
            if (percent == 100) {
                binding.tvProgressPercentage.visibility = View.GONE
                binding.ivCompletionTick.visibility = View.VISIBLE
            } else {
                binding.tvProgressPercentage.text = "$percent%"
                binding.tvProgressPercentage.visibility = View.VISIBLE
                binding.ivCompletionTick.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}