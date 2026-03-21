package com.app.hihlo.model.chat_history

data class ChatHistoryResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val isFirstTime: Int
    )
}