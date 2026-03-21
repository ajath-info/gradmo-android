package com.app.hihlo.model.get_profile

data class GetProfileResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload2,
    val status: Int
)