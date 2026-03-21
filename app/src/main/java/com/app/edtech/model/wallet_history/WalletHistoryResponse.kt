package com.app.hihlo.model.wallet_history

data class WalletHistoryResponse(
    val code: Int?=null,
    val error: Boolean?=null,
    val message: String?=null,
    val payload: Payload?=null,
    val status: Int?=null
) {
    data class Payload(
        val history: List<History>?=null,
        val pagination: Pagination?=null,
        val totalCoins: Int?=null,
        val user: User?=null
    ) {
        data class History(
            val caller_id: String?=null,
            val coins: Int?=null,
            val created_at: String?=null,
            val id: Int?=null,
            val sender_id: String?=null,
            val status: String?=null,
            val user_id: Int?=null,
            val user_name: String?=null,
            val user_profile_image: String?=null,
            val wallet_id: String?=null,
            val withdrawal_type: String?=null,
            val transaction_source: String?=null,
            val transaction_type: String?=null
        )

        data class Pagination(
            val currentPage: Int?=null,
            val limit: Int?=null,
            val totalPages: Int?=null,
            val totalRecords: Int?=null
        )

        data class User(
            val email: String?=null,
            val id: Int?=null,
            val name: String?=null,
            val profile_image: String?=null
        )
    }
}