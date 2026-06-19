package com.app.gradmo.model

data class InstituteCityResponse(
    val cities: List<City>,
    val msg: String,
    val status: String
) {
    data class City(
        val city: String
    )
}