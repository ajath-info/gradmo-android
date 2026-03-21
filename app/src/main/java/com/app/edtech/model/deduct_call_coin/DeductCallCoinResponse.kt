package com.app.hihlo.model.deduct_call_coin

data class DeductCallCoinResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val callId: Int,
        val callType: String,
        val coinsDeducted: Int,
        val remainingBalance: Int,
        val transactionId: Int,
        val userId: Int,
        val userRole: String,
        val walletId: String,
        val totalCoins: Int?=0,
        val requiredCoins: Int?=0,

    )
}