package com.app.hihlo.model.faq.response

data class FaqResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val faqsList: List<Faqs>
    ) {
        data class Faqs(
            val answer: String,
            val id: Int,
            val question: String
        )
    }
}