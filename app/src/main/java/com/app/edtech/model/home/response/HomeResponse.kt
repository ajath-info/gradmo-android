package com.app.hihlo.model.home.response

data class HomeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)