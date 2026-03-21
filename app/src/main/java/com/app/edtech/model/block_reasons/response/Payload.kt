package com.app.hihlo.model.block_reasons.response

data class Payload(
    val blockReasons: List<BlockReason>,
    val flagReasons: List<BlockReason>,
    val deleteReasons:List<BlockReason>
)

