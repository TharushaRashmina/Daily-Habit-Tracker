package com.example.dailyhabittracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.Habit
import org.threeten.bp.LocalDate

class HomeHabitsAdapter(
    private var habits: List<Habit>,
    private val onHabitClick: (Habit) -> Unit
) : RecyclerView.Adapter<HomeHabitsAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textHabitTitle: TextView = itemView.findViewById(R.id.textHabitTitle)
        private val checkboxHabit: CheckBox = itemView.findViewById(R.id.checkboxHabit)

        fun bind(habit: Habit) {
            val today = LocalDate.now().toString()
            val isCompleted = habit.isCompletedMap[today] ?: false

            textHabitTitle.text = habit.title
            checkboxHabit.isChecked = isCompleted

            // Set click listeners
            itemView.setOnClickListener { onHabitClick(habit) }
            checkboxHabit.setOnClickListener { onHabitClick(habit) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}
