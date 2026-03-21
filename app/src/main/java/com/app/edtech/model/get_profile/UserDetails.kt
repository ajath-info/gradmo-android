package com.app.hihlo.model.get_profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetails(
    val about: String? = null,
    val apple_id: String? = null,
    val city: String? = null,
    val country: String? = null,
    val created_at: String? = null,
    val device_token: String? = null,
    val device_type: String? = null,
    val dob: String? = null,
    val email: String? = null,
    val gender_id: Int? = null,
    val gender: String? = null,
    val interest_name: String? = null,
    val google_id: String? = null,
    val id: Int? = null,
    val interest_id: Int? = null,
    val is_verified: String? = null,
    val last_login: String? = null,
    val name: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val profile_image: String? = null,
    val role: String? = null,
    val social_type: String? = null,
    val status: String? = null,
    val updated_at: String? = null,
    val user_live_status: String? = null,
    val username: String? = null,
    val wallet_id: String? = null
):Parcelable
