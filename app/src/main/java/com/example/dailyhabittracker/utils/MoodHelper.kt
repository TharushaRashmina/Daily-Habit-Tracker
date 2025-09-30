package com.example.dailyhabittracker.utils

object MoodHelper {

    fun getMoodDescription(emoji: String): String {
        return when (emoji) {
            // Happy emotions
            "😊", "🙂", "☺️" -> "Happy"
            "😄", "😁", "😆" -> "Joyful"
            "😍", "🥰", "😘" -> "Love"
            "🤗", "🤩", "😃" -> "Excited"
            "😌", "🥲" -> "Content"
            "😇" -> "Peaceful"

            // Sad emotions
            "😢", "😭", "😿" -> "Sad"
            "😔", "😞", "😟" -> "Disappointed"
            "😕", "🙁", "☹️" -> "Unhappy"
            "😰", "😨", "😧" -> "Worried"
            "😥", "😓" -> "Stressed"
            "😪" -> "Tired"

            // Angry emotions
            "😠", "😡", "🤬" -> "Angry"
            "😤", "💢", "😾" -> "Frustrated"
            "🙄", "😒", "😑" -> "Annoyed"

            // Neutral/Other emotions
            "😐", "😶", "🤐" -> "Neutral"
            "🤔", "🧐", "🤨" -> "Thoughtful"
            "😴", "🥱" -> "Sleepy"
            "🤒", "🤧", "😷" -> "Sick"
            "😎", "🤓", "🥸" -> "Cool"
            "🤯", "😱", "😵" -> "Shocked"
            "😋", "😜", "🤪" -> "Playful"
            "🥺", "🥹" -> "Innocent"
            "😏", "😈", "😼" -> "Mischievous"

            // Default case
            else -> "Mixed feelings"
        }
    }

    fun getMoodCategory(emoji: String): String {
        return when (emoji) {
            "😊", "🙂", "☺️", "😄", "😁", "😆", "😍", "🥰", "😘",
            "🤗", "🤩", "😃", "😌", "😇", "🥲" -> "Positive"

            "😢", "😭", "😿", "😔", "😞", "😟", "😕", "🙁", "☹️",
            "😰", "😨", "😧", "😥", "😪", "😓" -> "Negative"

            "😠", "😡", "🤬", "😤", "💢", "😾", "🙄", "😒", "😑" -> "Angry"

            else -> "Neutral"
        }
    }

    fun getMoodColor(emoji: String): String {
        return when (getMoodCategory(emoji)) {
            "Positive" -> "#4CAF50" // Green
            "Negative" -> "#2196F3" // Blue
            "Angry" -> "#F44336" // Red
            else -> "#9E9E9E" // Gray
        }
    }
}
