package com.app.hihlo.model.home.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatorDetail(
    val city: String,
    val country: String,
    val gender_id: Int,
    val gender_name: String,
    val interest_id: Int,
    val interest_name: String,
    val interest_image: String,
    val is_creator: Int,
    val name: String,
    val profile_image: String,
    val user_live_status: String,
    val username: String
): Parcelable