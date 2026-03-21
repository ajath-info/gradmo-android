package com.app.hihlo.model.block_reasons.response

data class BlockReason(
    val block_reason: String,
    val flag_reason: String,
    var delete_reason: String?=null,
    val id: Int,
    var isSelected:Boolean?=false
)