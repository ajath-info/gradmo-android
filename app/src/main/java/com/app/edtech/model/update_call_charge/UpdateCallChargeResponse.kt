package com.app.hihlo.model.update_call_charge

data class UpdateCallChargeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val updatedCharges: UpdatedCharges,
        val userId: Int
    ) {
        data class UpdatedCharges(
            val audio_call: Int,
            val video_call: Int
        )
    }
}