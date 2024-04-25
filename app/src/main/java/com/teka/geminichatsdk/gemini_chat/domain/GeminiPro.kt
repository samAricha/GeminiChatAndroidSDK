package com.teka.geminichatsdk.gemini_chat.domain

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.ChatFutures
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.teka.geminichatsdk.BuildConfig
import com.teka.geminichatsdk.R
import com.teka.geminichatsdk.gemini_chat.data.ApiType
import com.teka.geminichatsdk.gemini_chat.data.ChatData
import com.teka.geminichatsdk.gemini_chat.data.ChatModel
import com.teka.geminichatsdk.gemini_chat.data.Message
import com.teka.geminichatsdk.gemini_chat.data.Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.util.concurrent.Executor

class GeminiPro {

    private var model: GenerativeModel? = null
    private var visionModel: GenerativeModel? = null
    private var chat: Chat? = null


    private val _conversationList = MutableLiveData(mutableStateListOf<Message>())
    val conversationList: LiveData<SnapshotStateList<Message>> = _conversationList



    suspend fun getResponse(
        chatModel: ChatFutures,
        query: String,
    ): ChatModel {

        val userMessageBuilder = Content.Builder()
        userMessageBuilder.role = "user"
        userMessageBuilder.text(query)
        val userMessage = userMessageBuilder.build()


        try {
            val response = withContext(Dispatchers.IO) {

                val resultMessage:  MutableLiveData<SnapshotStateList<Message>> = makeMultiTurnQuery(query)

                val result: MutableLiveData<String> = MutableLiveData()

                // Observing the LiveData for changes
                resultMessage.observeForever { snapshotStateList ->
                    if (!snapshotStateList.isEmpty()) {
                        // Extracting the text from the first Message in the list (assuming it's not empty)
                        val text = snapshotStateList[0].text
                        // Setting the value of the MutableLiveData with the extracted text
                        result.value = text
                    }
                }

                // Suspend coroutine until the LiveData emits a value
                while (result.value == null) {
                    delay(100) // Adjust delay as needed
                }

                // Extracting the text from the result MutableLiveData
                val responseText = result.value

                responseText

//                val chat = getModel().startChat(
//                    history = listOf(
//                        content(role = "user") { text("Hello, I have 2 dogs in my house.") },
//                        content(role = "model") { text("Great to meet you. What would you like to know?") }
//                    )
//                )
//
//                chat.sendMessage("How many paws are in my house?")


            }

            return ChatModel(
                prompt = response ?: "error",
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

    fun getModel(): GenerativeModel {
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
            modelName = "gemini-pro",
            apiKey = apiKey,
            generationConfig = generationConfig,
            safetySettings = listOf(harassmentSafety)
        )
        return gm
    }



    suspend fun makeMultiTurnQuery(
        prompt: String
    ): MutableLiveData<SnapshotStateList<Message>> {
        _conversationList.value?.add(
            Message(text = prompt, mode = Mode.USER)
        )
        _conversationList.value?.add(
            Message(
                text = "generating...",
                mode = Mode.GEMINI,
                isGenerating = true
            )
        )

        model = getModel()
        if (chat == null) {
            chat = getChat()
        }
        return makeGeneralQuery(ApiType.MULTI_CHAT, _conversationList, prompt)
    }


    private fun getChat() = getModel().startChat(generatePreviousChats())

    private fun generatePreviousChats(): List<Content> {
        val history = mutableListOf<Content>()
        for (message in conversationList.value.orEmpty()) {
            history.add(
                content(role = if (message.mode == Mode.USER) "user" else "model") {
                text(
                    message.text
                )
            })
        }
        return history
    }


    suspend fun makeGeneralQuery(
        apiType: ApiType,
        result: MutableLiveData<SnapshotStateList<Message>>,
        feed: Any
    ): MutableLiveData<SnapshotStateList<Message>> {
            var output = ""
            try {
                val stream =  chat?.sendMessageStream(feed as String)

                stream?.collect { chunk ->
                    output += chunk.text.toString()
                    output.trimStart()
                    result.value?.set(
                        result.value!!.lastIndex,
                        Message(text = output, mode = Mode.GEMINI, isGenerating = true)
                    )
                }
                result.value?.set(
                    result.value!!.lastIndex,
                    Message(text = output, mode = Mode.GEMINI, isGenerating = false)
                )

            } catch (e: Exception) {
                result.value?.set(
                    result.value!!.lastIndex,
                    Message(
                        text = e.message.toString(),
                        mode = Mode.GEMINI,
                        isGenerating = false
                    )
                )
            }

        return result

    }


}