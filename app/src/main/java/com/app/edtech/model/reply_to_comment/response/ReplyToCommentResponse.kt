package com.app.hihlo.model.reply_to_comment.response

data class ReplyToCommentResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)