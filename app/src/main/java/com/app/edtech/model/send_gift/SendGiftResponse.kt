package com.app.hihlo.model.send_gift

data class SendGiftResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val balances: Balances,
        val gift: Gift,
        val timestamp: String,
        val transaction: Transaction
    ) {
        data class Balances(
            val recipientNewBalance: Int,
            val senderNewBalance: Int
        )

        data class Gift(
            val coins: String,
            val recipientId: String,
            val recipientName: String,
            val recipientUsername: String,
            val reelId: Any,
            val type: String
        )

        data class Transaction(
            val recipientTransactionId: Int,
            val reelGiftId: Any,
            val senderTransactionId: Int
        )
    }
}