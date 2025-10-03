package com.example.dailyhabittracker.ui

import android.graphics.Color
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
    private lateinit var textPendingCount: TextView
    private lateinit var textAllDone: TextView
    private lateinit var chartMoodTrend: LineChart
    private lateinit var textChartInfo: TextView

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
        textPendingCount = view.findViewById(R.id.textPendingCount)
        textAllDone = view.findViewById(R.id.textAllDone)
        chartMoodTrend = view.findViewById(R.id.chartMoodTrend)
        textChartInfo = view.findViewById(R.id.textChartInfo)
    }

    private fun setupViews() {
        // Setup recycler view for today's habits - no click handling since it's just a display list
        adapter = HomeHabitsAdapter(emptyList())
        recyclerTodayHabits.layoutManager = LinearLayoutManager(requireContext())
        recyclerTodayHabits.adapter = adapter

        // Setup mood trend chart
        setupMoodChart()
    }

    private fun loadData() {
        val today = LocalDate.now()
        val todayString = today.toString()

        // Update date display
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        textDate.text = today.format(formatter)

        // Load habits for today - filter to show only incomplete habits
        val allHabits = prefs.habits
        val incompleteHabits = allHabits.filter { habit ->
            !(habit.isCompletedMap[todayString] ?: false)
        }

        adapter.updateHabits(incompleteHabits)

        // Update pending count and completion message
        updateToDoSection(incompleteHabits)

        // Calculate habit statistics using all habits
        updateHabitStats(allHabits, todayString)

        // Load recent mood
        updateMoodSection()

        // Update mood chart
        updateMoodChart()

        // Update greeting
        updateGreeting()
    }

    private fun updateToDoSection(incompleteHabits: List<Habit>) {
        val pendingCount = incompleteHabits.size

        if (pendingCount > 0) {
            // Show pending count badge
            textPendingCount.text = "$pendingCount pending"
            textPendingCount.visibility = View.VISIBLE
            textAllDone.visibility = View.GONE
            recyclerTodayHabits.visibility = View.VISIBLE
        } else {
            // Show completion message
            textPendingCount.visibility = View.GONE
            textAllDone.visibility = View.VISIBLE
            recyclerTodayHabits.visibility = View.GONE
        }
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

    private fun setupMoodChart() {
        chartMoodTrend.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // Configure X-axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.GRAY
                textSize = 10f
            }

            // Configure Y-axis
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textColor = Color.GRAY
                textSize = 10f
                axisMinimum = 0f
                axisMaximum = 5f
                granularity = 1f
                setDrawZeroLine(true)
                zeroLineColor = Color.GRAY
            }

            axisRight.isEnabled = false
            legend.isEnabled = false

            // Set no data text
            setNoDataText("No mood data available for this week")
            setNoDataTextColor(Color.GRAY)
        }
    }

    private fun updateMoodChart() {
        val weeklyMoodData = getWeeklyMoodData()

        if (weeklyMoodData.isEmpty()) {
            chartMoodTrend.clear()
            textChartInfo.text = "Add mood entries to see your weekly trend"
            return
        }

        val entries = mutableListOf<Entry>()
        val dayLabels = mutableListOf<String>()

        weeklyMoodData.forEachIndexed { index, (day, avgMood) ->
            entries.add(Entry(index.toFloat(), avgMood))
            dayLabels.add(day)
        }

        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = Color.parseColor("#FF9800") // Orange color
            setCircleColor(Color.parseColor("#FF9800"))
            lineWidth = 3f
            circleRadius = 6f
            setDrawCircleHole(false)
            valueTextSize = 0f // Hide value labels
            setDrawFilled(true)
            fillColor = Color.parseColor("#FFCC80")
            fillAlpha = 50
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
        }

        val lineData = LineData(dataSet)
        chartMoodTrend.data = lineData

        // Set custom labels for X-axis
        chartMoodTrend.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)

        // Set Y-axis labels
        chartMoodTrend.axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    1 -> "😢"
                    2 -> "😔"
                    3 -> "😐"
                    4 -> "😊"
                    5 -> "😄"
                    else -> ""
                }
            }
        }

        chartMoodTrend.invalidate()

        // Update chart info
        val avgMood = weeklyMoodData.map { it.second }.average()
        val trendText = when {
            avgMood >= 4.0 -> "Great week! You're feeling positive 😊"
            avgMood >= 3.0 -> "Balanced mood this week 😐"
            else -> "Take care of yourself this week 💙"
        }
        textChartInfo.text = trendText
    }

    private fun getWeeklyMoodData(): List<Pair<String, Float>> {
        val today = LocalDate.now()
        val weekStart = today.minusDays(6) // Last 7 days including today

        val weeklyData = mutableListOf<Pair<String, Float>>()
        val allMoods = prefs.moods

        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val dayName = when (i) {
                6 -> "Today"
                5 -> "Yesterday"
                else -> date.format(DateTimeFormatter.ofPattern("EEE"))
            }

            // Get moods for this day
            val dayMoods = allMoods.filter { mood ->
                // Convert timestamp (Long) to LocalDate using Calendar (compatible with API 24)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = mood.timestamp
                val moodYear = calendar.get(Calendar.YEAR)
                val moodMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
                val moodDay = calendar.get(Calendar.DAY_OF_MONTH)

                // Compare with target date
                date.year == moodYear && date.monthValue == moodMonth && date.dayOfMonth == moodDay
            }

            if (dayMoods.isNotEmpty()) {
                // Convert moods to numeric values and calculate average
                val numericMoods = dayMoods.map { mood ->
                    when (mood.emoji) {
                        "😢", "😭", "😰" -> 1f
                        "😔", "😞", "😕" -> 2f
                        "😐", "😑", "😶" -> 3f
                        "😊", "🙂", "😌" -> 4f
                        "😄", "😁", "😆", "😃" -> 5f
                        else -> 3f // Default neutral
                    }
                }
                val avgMood = numericMoods.average().toFloat()
                weeklyData.add(Pair(dayName, avgMood))
            }
        }

        return weeklyData
    }

    override fun onResume() {
        super.onResume()
        loadData() // Refresh data when returning to home
    }
}
