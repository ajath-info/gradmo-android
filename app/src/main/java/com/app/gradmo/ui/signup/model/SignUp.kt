package com.app.hihlo.ui.signup.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
@Keep
@Parcelize
data class SignUp(
    var name:String?=null,
    var username:String?=null,
    var phoneNumber:String?=null,
    var email:String?=null,
    var socialId:String?=null,
    var socialType:String?=null,
    var password:String?=null,
    var deviceType:String?=null,
    var deviceToken:String?=null,
    var dob:String?=null,
    var city:String?=null,
    var gender_id:String?=null,
    var interest_id:String?=null,
    var confirmPassword:String?=null
):Parcelable
