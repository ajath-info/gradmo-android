package com.app.gradmo.model.payment_history

data class PaymentHistoryResponse(
    val msg: String,
    val pagination: Pagination,
    val paymentData: List<PaymentData>,
    val status: String
) {
    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )

    data class PaymentData(
        val adminId: String,
        val amount: String,
        val batchId: String,
        val batchName: String,
        val batchOfferPrice: String,
        val batchPrice: String,
        val batchType: String,
        val createAt: String,
        val currencyCode: String,
        val currencyDecimalCode: String,
        val description: String,
        val endDate: String,
        val endTime: String,
        val id: String,
        val mode: String,
        val startDate: String,
        val startTime: String,
        val status: String,
        val transactionId: String
    )
}