package com.teka.geminichatsdk.gemini_chat.data

import android.graphics.Bitmap

data class ChatModel (
    val prompt: String,
    val bitmap: Bitmap?,
    val isFromUser: Boolean
)