package com.teka.geminichatsdk.spacee_gemini.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.teka.geminichatsdk.gemini_chat.data.Message

@Dao
interface MessageDao {
    @Upsert
    suspend fun upsertMessage(message: Message)

    @Query("SELECT * FROM message")
    fun getAllMessage(): LiveData<List<Message>>

    @Query("DELETE FROM message")
    suspend fun deleteAllMessages()
}