package com.app.gradmo.model.third_party_credentials

data class ThirdPartyCredentialsResponse(
    val msg: String,
    val payment_gateway_api_credentials: PaymentGatewayApiCredentials,
    val status: String,
    val zoom_api_credentials: ZoomApiCredentials
) {
    data class PaymentGatewayApiCredentials(
        val Key_id: String,
        val id: String,
        val mode: String,
        val paymentgateway: String,
        val secret_key: String,
        val status: String,
        val webhook_secret: String
    )

    data class ZoomApiCredentials(
        val android_api_key: String,
        val android_api_secret: String,
        val id: String,
        val meeting_sdk_key: String,
        val meeting_sdk_secret: String,
        val s2s_account_id: String,
        val s2s_client_id: String,
        val s2s_client_secret: String,
        val zoom_host_email: String,
        val zoom_host_user_id: String
    )
}