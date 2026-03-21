package com.app.hihlo.model.login.response

data class S3Detail(
    val ACCESS_KEY: String,
    val BUCKET_NAME: String,
    val REGION: String,
    val SECRET_KEY: String
)