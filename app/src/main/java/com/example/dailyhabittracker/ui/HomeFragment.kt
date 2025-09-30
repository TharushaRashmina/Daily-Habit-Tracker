package com.example.dailyhabittracker.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.Habit
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.ui.adapters.HomeHabitsAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var prefs: PreferencesHelper
    private lateinit var adapter: HomeHabitsAdapter

    // View references
    private lateinit var textGreeting: TextView
    private lateinit var textDate: TextView
    private lateinit var textHabitProgress: TextView
    private lateinit var textHabitPercentage: TextView
    private lateinit var progressHabits: ProgressBar
    private lateinit var textStreak: TextView
    private lateinit var textLatestMood: TextView
    private lateinit var textMoodTrend: TextView
    private lateinit var recyclerTodayHabits: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        prefs = PreferencesHelper(requireContext())

        initViews(view)
        setupViews()
        loadData()

        return view
    }

    private fun initViews(view: View) {
        textGreeting = view.findViewById(R.id.textGreeting)
        textDate = view.findViewById(R.id.textDate)
        textHabitProgress = view.findViewById(R.id.textHabitProgress)
        textHabitPercentage = view.findViewById(R.id.textHabitPercentage)
        progressHabits = view.findViewById(R.id.progressHabits)
        textStreak = view.findViewById(R.id.textStreak)
        textLatestMood = view.findViewById(R.id.textLatestMood)
        textMoodTrend = view.findViewById(R.id.textMoodTrend)
        recyclerTodayHabits = view.findViewById(R.id.recyclerTodayHabits)
    }

    private fun setupViews() {
        // Setup recycler view for today's habits
        adapter = HomeHabitsAdapter(emptyList()) { habit ->
            toggleHabit(habit)
        }
        recyclerTodayHabits.layoutManager = LinearLayoutManager(requireContext())
        recyclerTodayHabits.adapter = adapter
    }

    private fun loadData() {
        val today = LocalDate.now()
        val todayString = today.toString()

        // Update date display
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        textDate.text = today.format(formatter)

        // Load habits for today
        val habits = prefs.habits
        adapter.updateHabits(habits)

        // Calculate habit statistics
        updateHabitStats(habits, todayString)

        // Load recent mood
        updateMoodSection()

        // Update greeting
        updateGreeting()
    }

    private fun updateHabitStats(habits: List<Habit>, todayString: String) {
        val completedToday = habits.count { it.isCompletedMap[todayString] == true }
        val totalHabits = habits.size

        textHabitProgress.text = "$completedToday of $totalHabits habits completed"

        if (totalHabits > 0) {
            val percentage = (completedToday * 100) / totalHabits
            progressHabits.progress = percentage
            textHabitPercentage.text = "$percentage%"
        } else {
            progressHabits.progress = 0
            textHabitPercentage.text = "0%"
        }

        // Update streak information
        updateStreakInfo(habits)
    }

    private fun updateStreakInfo(habits: List<Habit>) {
        val maxStreak = habits.maxOfOrNull { calculateStreak(it) } ?: 0
        textStreak.text = "Best streak: $maxStreak days"
    }

    private fun calculateStreak(habit: Habit): Int {
        var streak = 0
        var currentDate = LocalDate.now()

        while (habit.isCompletedMap[currentDate.toString()] == true) {
            streak++
            currentDate = currentDate.minusDays(1)
        }

        return streak
    }

    private fun updateMoodSection() {
        val recentMoods = prefs.moods.sortedByDescending { it.timestamp }.take(3)

        if (recentMoods.isNotEmpty()) {
            val latestMood = recentMoods.first()
            textLatestMood.text = latestMood.emoji

            // Show recent mood trend
            val moodEmojis = recentMoods.map { it.emoji }.joinToString(" ")
            textMoodTrend.text = "Recent: $moodEmojis"
        } else {
            textLatestMood.text = "😊"
            textMoodTrend.text = "No mood entries yet"
        }
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good Morning!"
            in 12..17 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
        textGreeting.text = greeting
    }

    private fun toggleHabit(habit: Habit) {
        val today = LocalDate.now().toString()
        habit.isCompletedMap[today] = !(habit.isCompletedMap[today] ?: false)
        prefs.saveHabit(habit)
        loadData() // Refresh the view
    }

    override fun onResume() {
        super.onResume()
        loadData() // Refresh data when returning to home
    }
}
