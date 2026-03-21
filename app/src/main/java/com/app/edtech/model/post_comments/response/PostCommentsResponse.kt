package com.app.hihlo.model.post_comments.response

data class PostCommentsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)