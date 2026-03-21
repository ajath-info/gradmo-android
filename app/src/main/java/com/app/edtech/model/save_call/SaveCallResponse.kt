package com.app.hihlo.model.save_call

data class SaveCallResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val call_id: Int,
        val call_type: String,
        val caller_id: Int,
        val coins_deducted: Int,
        val duration_seconds: Any,
        val ended_at: Any,
        val receiver_id: Int,
        val started_at: String,
        val status: String
    )
}