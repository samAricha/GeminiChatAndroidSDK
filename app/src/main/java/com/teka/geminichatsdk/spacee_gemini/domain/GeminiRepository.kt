package com.teka.geminichatsdk.spacee_gemini.domain

import com.teka.geminichatsdk.spacee_gemini.data.ChatStatusModel


interface GeminiRepository {
    suspend fun generate(
        prompt: String,
        images: List<ByteArray> = emptyList()
    ): ChatStatusModel

    fun getApiKey(): String

}
