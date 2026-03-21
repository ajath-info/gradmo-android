package com.app.hihlo.model.get_recent_chat.response

data class GetRecentChatResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)