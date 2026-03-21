package com.app.hihlo.model.common_response

data class CommonResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val status: Int
)