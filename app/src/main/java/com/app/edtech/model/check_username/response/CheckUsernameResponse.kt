package com.app.hihlo.model.check_username.response

data class CheckUsernameResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val usernameAvailable: Int
    )
}