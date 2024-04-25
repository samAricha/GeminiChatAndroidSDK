package com.teka.geminichatsdk.spacee_gemini

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teka.geminichatsdk.gemini_chat.data.Message
import com.teka.geminichatsdk.spacee_gemini.MessageDao

@Database(
    entities = [Message::class],
    version = 1
)
abstract class MessageDatabase : RoomDatabase() {
    abstract val dao: MessageDao
}