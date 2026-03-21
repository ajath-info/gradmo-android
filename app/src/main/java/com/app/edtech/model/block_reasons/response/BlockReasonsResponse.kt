package com.app.hihlo.model.block_reasons.response

data class BlockReasonsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)