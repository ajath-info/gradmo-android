package com.app.hihlo.model.save_recent_chat.response

data class SaveRecentChatResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)