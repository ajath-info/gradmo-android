package com.app.edtech.model.login.request

data class LoginRequest(
    val username: String ?= null,
    val password: String ?= null,
    val user_type: String ?= null,
    val device_id: String ?= null,
    val device_token: String ?= null,
    val device_type: String ?= null,
    val mobile: String ?= null,
)