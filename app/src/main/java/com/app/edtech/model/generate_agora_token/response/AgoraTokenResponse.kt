package com.app.hihlo.model.generate_agora_token.response

data class AgoraTokenResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)