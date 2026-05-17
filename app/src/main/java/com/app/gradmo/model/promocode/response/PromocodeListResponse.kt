package com.app.gradmo.model.promocode.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PromocodeListResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val pagination: Pagination,
        val promoCodes: List<PromoCode>
    ) {
        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )

        @Parcelize
        data class PromoCode(
            val code: String,
            val createdAt: String,
            val discountType: String,
            val discountValue: Int,
            val maxUse: Int,
            val promoCodeId: Int,
            val status: Int,
            val usedCount: Int,
            val validFrom: String,
            val validTo: String
        ): Parcelable
    }
}