package com.app.edtech.model.profile.request

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val user_type: String? = null,
    val address: String? = null,
    val country: String? = null,
    val state: String? = null,
    val city: String? = null,
    val image: String? = null,
    val pincode: String? = null,
    val school_college_name: String? = null,
    val grade: String? = null,
    val student_id: String? = null
)