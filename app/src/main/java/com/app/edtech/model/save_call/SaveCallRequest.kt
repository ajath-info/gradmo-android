package com.app.hihlo.model.save_call

data class SaveCallRequest(
    val call_type: String?=null,
    val ended_at: String?=null,
    val receiver_id: Int?=null,
    val started_at: String?=null,
    val status: String?=null
)