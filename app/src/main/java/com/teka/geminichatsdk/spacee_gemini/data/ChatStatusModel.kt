package com.teka.geminichatsdk.spacee_gemini.data
sealed class ChatStatusModel {
    data object Idle : ChatStatusModel()
    class Success(val data: String) : ChatStatusModel()
    class Error(val message: String) : ChatStatusModel()
    data object Loading : ChatStatusModel()
}