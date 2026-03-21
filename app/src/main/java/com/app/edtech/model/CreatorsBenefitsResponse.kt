package com.app.edtech.model

data class CreatorsBenefitsResponse(
    val code: Int?=null,
    val error: Boolean?=null,
    val message: String?=null,
    val payload: Payload?=null,
    val status: Int?=null
)

data class Payload(
    val benefitsList: List<Benefits>?=null,
    val otp: String?=null
)

data class Benefits(
    val benefit_text: String?=null,
    val created_at: String?=null,
    val display_order: Int?=null,
    val id: Int?=null,
    val status: String?=null,
    val updated_at: String?=null
)

data class SendOtpPhoneRequest(
    var phone: String?=null
)

data class VerifyPhoneOtpRequest(
    var phone: String?=null,
    var otp: String?=null
)


data class UserToCreatorRequest(
    var imageUrls: List<String>?=null,
    var videoUrl: String?=null,
)