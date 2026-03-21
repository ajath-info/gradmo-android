package com.app.hihlo.model.send_gift

data class SendGiftRequest(
    val coins: String? = null,
    val recipientId: String? = null,
    val type: String? = null,
    val reelId: String? = null,
)
