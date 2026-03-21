package com.app.hihlo.model.login.response

data class LoginResponse(
    val code: Int?=null,
    val error: Boolean?=null,
    val message: String?=null,
    val payload: Payload?=null,
    val status: Int?=null
)