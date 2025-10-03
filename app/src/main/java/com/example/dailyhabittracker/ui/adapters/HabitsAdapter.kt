package com.example.dailyhabittracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.databinding.ItemHabitBinding
import com.example.dailyhabittracker.data.Habit
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime


class HabitsAdapter(
    private var habits: MutableList<Habit>,
    val onToggle: (Habit) -> Unit,
    val onEdit: (Habit) -> Unit,
    val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitVH>() {

    class HabitVH(val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitVH {
        val b = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitVH(b)
    }

    override fun onBindViewHolder(holder: HabitVH, position: Int) {
        val habit = habits[position]
        holder.binding.tvHabitTitle.text = habit.title
        val today = LocalDate.now().toString()
        val isCompleted = habit.isCompletedMap[today] == true

        // Set checkbox state without triggering listener
        holder.binding.cbCompleted.setOnClickListener(null)
        holder.binding.cbCompleted.isChecked = isCompleted

        // Set click listener after setting the state
        holder.binding.cbCompleted.setOnClickListener {
            onToggle(habit)
        }

        holder.binding.btnEdit.setOnClickListener { onEdit(habit) }
        holder.binding.btnDelete.setOnClickListener { onDelete(habit) }
    }

    override fun getItemCount(): Int = habits.size

    fun updateData(newList: MutableList<Habit>) {
        habits = newList
        notifyDataSetChanged()
    }
}