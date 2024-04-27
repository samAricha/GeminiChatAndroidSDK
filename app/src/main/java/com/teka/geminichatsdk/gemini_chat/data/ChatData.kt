package com.teka.geminichatsdk.gemini_chat.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.ChatFutures
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.teka.geminichatsdk.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {


    val api_key = BuildConfig.GEMINI_KEY


    suspend fun getResponse(prompt: String): ChatModel {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = api_key
        )

//        val generativeModel = getChatModel()

        val chat = generativeModel.startChat(
//            history = listOf(
//                content(role = "user") { text("Hello, I have 2 dogs in my house.") },
////                content(role = "model") { text("Great to meet you. What would you like to know?") }
//            )
        )

        try {
            val response = withContext(Dispatchers.IO) {
                chat.sendMessage(prompt)
//                generativeModel.generateContent(prompt)
            }

            return ChatModel(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            return ChatModel(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }

    }

    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): ChatModel {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision", apiKey = api_key
        )

        try {

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            return ChatModel(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            return ChatModel(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }

    }


    private fun getChatModel(): ChatFutures {
        val modelFutures: GenerativeModelFutures = getGeminiModel()
        return modelFutures.startChat()
    }

    fun getGeminiModel(): GenerativeModelFutures {
        val apiKey: String = BuildConfig.GEMINI_KEY
        val harassmentSafety = SafetySetting(
            HarmCategory.HARASSMENT,
            BlockThreshold.ONLY_HIGH
        )

        val configBuilder: GenerationConfig.Builder = GenerationConfig.Builder()

        configBuilder.temperature = 0.9f
        configBuilder.topK = 16
        configBuilder.topP = 0.1f
        val generationConfig: GenerationConfig = configBuilder.build()
        val gm = GenerativeModel(
            "gemini-pro",
            apiKey,
            generationConfig,
            listOf(harassmentSafety)
        )
        return GenerativeModelFutures.from(gm)
    }


}




















