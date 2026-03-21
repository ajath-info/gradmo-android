package com.app.hihlo.model.save_recent_chat.response

data class Payload(
    val fromUserId: String,
    val message: String,
    val toUserId: String
)