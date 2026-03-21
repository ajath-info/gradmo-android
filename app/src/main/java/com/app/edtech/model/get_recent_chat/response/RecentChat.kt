package com.app.hihlo.model.get_recent_chat.response

import com.app.hihlo.model.get_profile.UserDetailsX

data class RecentChat(
    val chatCreatedAt: String? = null,
    val chatId: Int? = null,
    val chatUpdatedAt: String? = null,
    val fromUserId: Int? = null,
    val message: String? = null,
    val readStatus: String? = null,
    val toUserId: Int? = null,
    val messageSentBy: Int? = null,
    val userDetails: UserDetailsX? = null
)