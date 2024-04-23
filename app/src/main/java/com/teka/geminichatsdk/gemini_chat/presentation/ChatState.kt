package com.teka.geminichatsdk.gemini_chat.presentation

import android.graphics.Bitmap
import com.teka.geminichatsdk.gemini_chat.data.ChatModel

data class ChatState (
    val chatList: MutableList<ChatModel> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)