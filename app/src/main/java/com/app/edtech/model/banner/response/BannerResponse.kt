package com.app.edtech.model.banner.response

data class BannerResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val banners: List<Banner>,
        val pagination: Pagination
    ) {
        data class Banner(
            val description: String,
            val heading: String,
            val id: Int,
            val image_url: String,
            val subheading: String
        )

        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )
    }
}