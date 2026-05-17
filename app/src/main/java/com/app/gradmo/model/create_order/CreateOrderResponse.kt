package com.app.gradmo.model.create_order

data class CreateOrderResponse(
    val keyId: String,
    val msg: String,
    val order: Order,
    val status: String
) {
    data class Order(
        val amount: Int,
        val currency: String,
        val id: String,
        val receipt: String,
        val status: String
    )
}