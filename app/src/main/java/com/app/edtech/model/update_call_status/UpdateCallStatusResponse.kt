package com.app.hihlo.model.update_call_status

data class UpdateCallStatusResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val call_id: Int,
        val duration_seconds: Any,
        val status: String
    )
}