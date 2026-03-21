package com.app.hihlo.model.city_list.response

data class CityListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)