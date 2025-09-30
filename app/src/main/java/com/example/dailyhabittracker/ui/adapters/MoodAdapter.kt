package com.example.dailyhabittracker.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.MoodEntry
import com.example.dailyhabittracker.utils.MoodHelper
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(private var moods: MutableList<MoodEntry>) : RecyclerView.Adapter<MoodAdapter.MoodVH>() {

    class MoodVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        val tvMoodText: TextView = itemView.findViewById(R.id.tvMoodText)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val viewMoodIndicator: View = itemView.findViewById(R.id.viewMoodIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
        return MoodVH(view)
    }

    override fun onBindViewHolder(holder: MoodVH, position: Int) {
        val mood = moods[position]

        // Set emoji
        holder.tvEmoji.text = mood.emoji

        // Set mood description using our helper
        val moodDescription = MoodHelper.getMoodDescription(mood.emoji)
        holder.tvMoodText.text = moodDescription

        // Set mood color based on category
        val moodColor = MoodHelper.getMoodColor(mood.emoji)
        holder.tvMoodText.setTextColor(Color.parseColor(moodColor))

        // Set the mood indicator color
        holder.viewMoodIndicator.setBackgroundColor(Color.parseColor(moodColor))

        // Set date
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(mood.timestamp))
    }

    override fun getItemCount(): Int = moods.size

    fun updateData(newList: MutableList<MoodEntry>) {
        moods = newList
        notifyDataSetChanged()
    }
}