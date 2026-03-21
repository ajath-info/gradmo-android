package com.app.hihlo.model.coin_details

data class CoinDetailsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val coins: Int,
        val userDetails: UserDetails
    ) {
        data class UserDetails(
            val id: Int,
            val walletId: String
        )
    }
}