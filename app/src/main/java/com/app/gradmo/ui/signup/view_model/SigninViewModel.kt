package com.app.gradmo.ui.signup.view_model

import android.app.Application
import android.util.Patterns
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.gradmo.R
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.preferences.USER_TYPE
import com.app.gradmo.model.login.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigninViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    val email = ObservableField<String>()
    val phone = ObservableField<String>()
    val password = ObservableField<String>()
    val isTermsChecked = MutableLiveData(true)
    val loginType = MutableLiveData("password")


    private val _validationMessage = MutableLiveData<String>()
    val validationMessage: LiveData<String> = _validationMessage

    private val loginLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getLoginLiveData(): LiveData<Resources<LoginResponse>> {
        return loginLiveDate
    }
    fun hitLoginDataApi(request: LoginRequest) {

        try {
            loginLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    loginLiveDate.postValue(
                        Resources.success(
                            ApiRepository().loginApi(request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    loginLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }



    private val sendLoginOtpLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getSendLoginOtpLiveData(): LiveData<Resources<LoginResponse>> {
        return sendLoginOtpLiveDate
    }
    fun hitSendLoginOtpDataApi(request: LoginRequest) {

        try {
            sendLoginOtpLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    sendLoginOtpLiveDate.postValue(
                        Resources.success(
                            ApiRepository().sendLoginOtpApi(request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    sendLoginOtpLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun onClick(view: View) {
        when(view.id){
            R.id.loginButton->{
                onSubmit()
            }
        }
    }

    fun onSubmit() {
        val emailInput = email.get()?.trim()
        val phoneInput = phone.get()?.trim()
        val passwordInput = password.get()?.trim()
        if (loginType.value=="password"){
            if (emailInput.isNullOrEmpty()) {
                _validationMessage.value = "Please Enter Email"
                return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                _validationMessage.value = "Please Enter Valid Email"
                return
            }
            if (passwordInput.isNullOrEmpty()) {
                _validationMessage.value = "Please Enter Password"
                return
            }
//        val deviceToken = Preferences.getStringPreference(getApplication(), FCM_TOKEN)
            val deviceToken = "test"
            val userType = Preferences.getStringPreference(getApplication(), USER_TYPE)
            val deviceId = "test"
            hitLoginDataApi(LoginRequest(username = emailInput, password = passwordInput, device_token = deviceToken, device_id = deviceId, device_type = "android", user_type = userType))

        }else {
            if (phoneInput.isNullOrEmpty()) {
                _validationMessage.value = "Please Enter Phone Number"
                return
            }
            val userType = Preferences.getStringPreference(getApplication(), USER_TYPE)
            hitSendLoginOtpDataApi(LoginRequest(mobile = phoneInput, user_type = userType))
        }
//        if (isTermsChecked.value != true) {
//            _validationMessage.value = "Please agree to the terms"
//            return
//        }
    }

}