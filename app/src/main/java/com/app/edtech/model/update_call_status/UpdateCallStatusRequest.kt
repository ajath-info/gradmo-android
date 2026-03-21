package com.app.hihlo.model.update_call_status

data class UpdateCallStatusRequest(
    val call_id: Int,
    val status: String
)