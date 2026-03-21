package com.app.hihlo.model.contact_us.request

data class ContactUsRequest(
    val message: String? = null,
    val phone: String? = null,
    val imageUrl: String? = null,
)
