package com.teka.geminichatsdk.gemini_chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    val text: String,
    val isGenerating: Boolean = false,
    val mode: Mode,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)