package com.example.dailyhabittracker.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.dailyhabittracker.R

class EmojiDialog(private val onSelect: (String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_emoji, null)
        val emojis = arrayOf("😄","🙂","😐","😔","😢","😡","😴","🤩")
        // simple grid of buttons; each button id: btn1..btn8
        val btnIds = listOf(
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8
        )
        for (i in emojis.indices) {
            val b = view.findViewById<View>(btnIds[i])
            b?.setOnClickListener { onSelect(emojis[i]); dismiss() }
        }
        return AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(view)
            .setNegativeButton("Cancel", null)
            .create()
    }
}