package com.example.dailyhabittracker.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.Habit

class AddEditHabitDialog(private val existing: Habit? = null, private val onSave: (Habit) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val etTitle = view.findViewById<EditText>(R.id.etHabitTitle)
        if (existing != null) etTitle.setText(existing.title)

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Add Habit" else "Edit Habit")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val title = etTitle.text.toString().trim()
                if (title.isNotEmpty()) {
                    val habit = existing ?: Habit(System.currentTimeMillis(), title)
                    habit.title = title
                    onSave(habit)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}