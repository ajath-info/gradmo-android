package com.app.hihlo.model.post_comments.response

data class Payload(
    val comment: String,
    val id: Int,
    val reel_id: String,
    val user_id: Int
)