package com.app.hihlo.model.edit_profile.response

data class User(
    val about: String,
    val city: String,
    val country: String,
    val dob: String,
    val email: String,
    val interest_id: Int,
    val name: String,
    val phone: String,
    val profile_image: Any,
    val username: String
)