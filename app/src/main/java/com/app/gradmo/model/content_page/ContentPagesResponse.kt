package com.app.gradmo.model.content_page

data class ContentPagesResponse(
    val `data`: Data,
    val msg: String,
    val status: String
) {
    data class Data(
//        val Delete Policy: DeletePolicy,
        val about_us: AboutUs,
        val privacy_policy: PrivacyPolicy,
        val terms_condition: TermsCondition
    ) {
        data class DeletePolicy(
            val content: String,
            val createdAt: String,
            val id: Int,
            val key: String,
            val subject: String,
            val updatedAt: String
        )

        data class AboutUs(
            val content: String,
            val createdAt: String,
            val id: Int,
            val key: String,
            val subject: String,
            val updatedAt: String
        )

        data class PrivacyPolicy(
            val content: String,
            val createdAt: String,
            val id: Int,
            val key: String,
            val subject: String,
            val updatedAt: String
        )

        data class TermsCondition(
            val content: String,
            val createdAt: String,
            val id: Int,
            val key: String,
            val subject: String,
            val updatedAt: String
        )
    }
}