package com.app.hihlo.model.deduct_call_coin

data class DeductCallCoinRequest(
    val call_id: Int?=null,
    val call_type: String?=null,
    val receiver_id: Int?=null
)