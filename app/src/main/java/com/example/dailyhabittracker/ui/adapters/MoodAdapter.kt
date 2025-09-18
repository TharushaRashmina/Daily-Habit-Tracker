package com.example.dailyhabittracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhabittracker.databinding.ItemMoodBinding
import com.example.dailyhabittracker.data.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(private var moods: MutableList<MoodEntry>) : RecyclerView.Adapter<MoodAdapter.MoodVH>() {
    class MoodVH(val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodVH {
        val b = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodVH(b)
    }

    override fun onBindViewHolder(holder: MoodVH, position: Int) {
        val m = moods[position]
        holder.binding.tvEmoji.text = m.emoji
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(m.timestamp))
    }

    override fun getItemCount(): Int = moods.size

    fun updateData(newList: MutableList<MoodEntry>) {
        moods = newList
        notifyDataSetChanged()
    }
}