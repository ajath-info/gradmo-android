package com.app.hihlo.model.recharge_package.response

data class RechargePackageListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: List<Payload>,
    val status: Int
) {
    data class Payload(
        val base_price: String,
        val coins: Int,
        val final_price: String,
        val gst_rate: String,
        val id: Int,
        val is_most_popular: Int
    )
}