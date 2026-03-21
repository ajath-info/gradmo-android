package com.app.hihlo.model.reel.response

data class ReelsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)