package com.app.hihlo.model.get_recent_chat.response

import com.app.hihlo.model.chat.RecentChats

data class Payload(
    val recentChats: List<RecentChat>,
    val summary: Summary,
    val totalUsers: Int
)