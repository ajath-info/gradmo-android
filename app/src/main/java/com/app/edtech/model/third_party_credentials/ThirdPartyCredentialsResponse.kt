package com.app.edtech.model.third_party_credentials

data class ThirdPartyCredentialsResponse(
    val msg: String,
    val payment_gateway_api_credentials: PaymentGatewayApiCredentials,
    val status: String,
    val zoom_api_credentials: List<Any>
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
}