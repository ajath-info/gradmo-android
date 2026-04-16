package com.app.edtech.model.address.state

data class GetStatesResponse(
    val countryId: Int,
    val msg: String,
    val pagination: Pagination,
    val states: List<State>,
    val status: String
) {
    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )

    data class State(
        val countryId: Int,
        val id: Int,
        val name: String
    )
}