package com.app.hihlo.model.get_recent_chat.response

data class Summary(
    val creatorsCount: Int,
    val onlineUsers: Int,
    val totalChats: Int,
    val uniqueUsers: Int,
    val usersWithStories: Int
)