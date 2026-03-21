package com.app.hihlo.model.home.response

data class Filters(
    val applied_gender_filter: Int?=null,
    val available_genders: List<AvailableGender>
)