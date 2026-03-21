package com.app.hihlo.model.gender_list

data class GenderListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)