package com.app.hihlo.model.home.response

data class Pagination(
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalPosts: Int
)