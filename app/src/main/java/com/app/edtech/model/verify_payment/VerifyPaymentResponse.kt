package com.app.edtech.model.verify_payment

data class VerifyPaymentResponse(
    val ledger: Ledger,
    val msg: String,
    val payment: Payment,
    val recordedInHistory: Int,
    val status: Boolean
)

data class Ledger(
    val payments_id: Int,
    val payments_inserted: Int,
    val student_batchs_id: Int,
    val student_batchs_inserted: Int,
    val student_payment_history_id: Int
)

data class Payment(
    val amountPaise: Int,
    val currency: String,
    val gatewayStatus: String,
    val id: String,
    val orderId: String
)