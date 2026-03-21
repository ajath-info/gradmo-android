package com.app.hihlo.model.edit_profile.request

data class EditProfileRequest(
    val name: String = "Ujj",
    val username: String = "",
    val phone: String = "",
    val dob: String = "",
    val gender_id: String = "",
    val email: String = "",
    val city: String = "",
    val country: String = "",
    val interestName: String = "",
    val profileImageUrl: String = "",
    val about: String = ""
)
