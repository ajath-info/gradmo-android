package com.app.hihlo.model.ads_list

data class GetAdsListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val ads: List<Ad>
    ) {
        data class Ad(
            val createdAt: String,
            val description: String,
            val id: Int,
            val imageUrl: String,
            val title: String,
            val updatedAt: String
        )
    }
}