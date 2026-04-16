package com.app.edtech.model.address.city

data class GetCitiesResponse(
    val cities: List<City>,
    val msg: String,
    val pagination: Pagination,
    val stateId: Int,
    val status: String
) {
    data class City(
        val city: String,
        val id: Int,
        val stateId: Int
    )

    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )
}