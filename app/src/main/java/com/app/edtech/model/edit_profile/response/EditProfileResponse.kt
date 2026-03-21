package com.app.hihlo.model.edit_profile.response

data class EditProfileResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)