package com.app.hihlo.model.home.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetails(
    val name: String? = null,
    val username: String? = null,
    val profile_image: String? = null,
    val user_live_status: String? = null,
    val city: String? = null,
    val country: String? = null,
    val gender_id: String? = null,
    val gender_name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val is_creator: String? = null
) : Parcelable
