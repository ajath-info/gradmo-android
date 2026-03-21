package com.app.hihlo.model.save_recent_chat.request

data class SaveRecentChatRequest(
    val toUserId: String? = null,
    val message: String? = null,
)
