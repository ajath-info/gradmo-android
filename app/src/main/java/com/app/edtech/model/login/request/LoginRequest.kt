package com.app.hihlo.model.login.request

data class LoginRequest(
    val email: String ?= null,
    val password: String ?= null,
    val deviceToken: String ?= null,
    val deviceType: String ?= null,
)