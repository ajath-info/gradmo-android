package com.app.edtech.ui.signup.view_model

import android.app.Application
import android.util.Patterns
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edtech.R
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.preferences.FCM_TOKEN
import com.app.edtech.preferences.Preferences
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import com.app.hihlo.model.login.request.LoginRequest
import com.app.hihlo.model.login.response.LoginResponse
import com.app.hihlo.ui.signup.model.SocialSignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigninViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    val email = ObservableField<String>()
    val password = ObservableField<String>()
    val isTermsChecked = MutableLiveData(true)


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
    fun onClick(view: View) {
        when(view.id){
//            R.id.loginButton->{
//                onSubmit()
//            }
        }
    }

    fun onSubmit() {
        val emailInput = email.get()?.trim()
        val passwordInput = password.get()?.trim()

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
        if (isTermsChecked.value != true) {
            _validationMessage.value = "Please agree to the terms"
            return
        }


        // Success
        hitLoginDataApi(LoginRequest(email = emailInput, password = passwordInput, deviceToken = Preferences.getStringPreference(getApplication(), FCM_TOKEN), deviceType = "A", ))
//        _validationMessage.value = "Successful!"
    }

    private val socialLoginLiveData = SingleLiveEvent<Resources<LoginResponse>>()

    fun getSocialLiveData(): LiveData<Resources<LoginResponse>> {
        return socialLoginLiveData
    }
    fun hitSocialApi(request: SocialSignUpRequest) {

        try {
            socialLoginLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    socialLoginLiveData.postValue(
                        Resources.success(
                            ApiRepository().socialLogin(request
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

}