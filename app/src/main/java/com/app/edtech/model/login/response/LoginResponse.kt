package com.app.edtech.model.login.response

import com.app.hihlo.model.login.response.Payload

data class LoginResponse(
    val status: String? = null,
    val msg: String? = null,
    val otp: String? = null,
    val data: UserData? = null
)

data class UserData(
    val userType: String? = null,
    val studentId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val enrollmentId: String? = null,
    val image: String? = null,
    val device_id: String? = null,
    val device_token: String? = null,
    val device_type: String? = null,
    val is_profile_completed: Int? = null,
    val access_token: String? = null,
    val token_type: String? = null,
    val address: String? = null,
    val country: String? = null,
    val pincode: String? = null,
    val city: String? = null,
    val state: String? = null,
    val schoolCollegeName: String? = null,
    val grade: String? = null,
)