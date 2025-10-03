package com.example.dailyhabittracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.Habit

class HomeHabitsAdapter(
    private var habits: List<Habit>,
) : RecyclerView.Adapter<HomeHabitsAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textHabitTitle: TextView = itemView.findViewById(R.id.textHabitTitle)

        fun bind(habit: Habit) {
            textHabitTitle.text = habit.title
            // No click listener needed - this is just a display list
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
