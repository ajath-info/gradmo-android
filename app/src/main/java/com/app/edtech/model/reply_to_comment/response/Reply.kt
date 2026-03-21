package com.app.hihlo.model.reply_to_comment.response

data class Reply(
    val created_at: String,
    val id: Int,
    val reply: String,
    val updated_at: String,
    val user: User
)