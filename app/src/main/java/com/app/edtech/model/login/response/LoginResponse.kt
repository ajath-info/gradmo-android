package com.app.edtech.model.login.response

import com.app.hihlo.model.login.response.Payload
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val status: String? = null,
    val msg: String? = null,
    val otp: String? = null,
    val data: UserData? = null
)

data class UserData(

    val userType: String? = null,

    @SerializedName("studentId")
    val studentId: Int? = null,

    @SerializedName("adminId")
    val adminId: Int? = null,

    val name: String? = null,
    val email: String? = null,
    val mobile: String? = null,

    @SerializedName("contactNo")
    val contactNo: String? = null,

    @SerializedName("mobileAlt")
    val mobileAlt: String? = null,

    val enrollmentId: String? = null,
    val multiBatch: String? = null,
    val gender: String? = null,
    val dob: String? = null,

    @SerializedName("fatherName")
    val fatherName: String? = null,

    @SerializedName("fatherDesignation")
    val fatherDesignation: String? = null,

    val address: String? = null,
    val pincode: String? = null,
    val country: String? = null,
    val state: String? = null,
    val city: String? = null,

    @SerializedName("batchId")
    val batchId: String? = null,

    @SerializedName("admissionDate")
    val admissionDate: String? = null,

    val status: Int? = null,
    val loginStatus: Int? = null,
    val paymentStatus: Int? = null,

    val appVersion: String? = null,
    val payMode: Int? = null,

    val schoolCollegeName: String? = null,
    val grade: String? = null,

    @SerializedName("isVerified")
    val isVerified: String? = null,

    val userTypeDb: String? = null,
    val addedBy: String? = null,

    @SerializedName("lastLoginApp")
    val lastLoginApp: String? = null,

    val updatedAt: String? = null,
    val image: String? = null,

    @SerializedName("device_id")
    val deviceId: String? = null,

    @SerializedName("device_token")
    val deviceToken: String? = null,

    @SerializedName("device_type")
    val deviceType: String? = null,

    @SerializedName("is_profile_completed")
    val isProfileCompleted: Int? = null,

    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("token_type")
    val tokenType: String? = null
)