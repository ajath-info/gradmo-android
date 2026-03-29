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
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.USER_TYPE
import com.app.edtech.preferences.UserPreference
import com.app.hihlo.ui.signup.model.SocialSignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class JoinViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    val email = ObservableField<String>()
    val password = ObservableField<String>()
    val name = ObservableField<String>()
    val phone = ObservableField<String>()
    val isTermsChecked = MutableLiveData(true)


    private val _validationMessage = MutableLiveData<String>()
    val validationMessage: LiveData<String> = _validationMessage

    fun onClick(view: View) {
        when(view.id){
            R.id.getOtpButton->{
                onSubmit()
            }
        }
    }
    private val signUpLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getSignUpLiveData(): LiveData<Resources<LoginResponse>> {
        return signUpLiveDate
    }
    fun hitSignUpDataApi(request: LoginRequest) {

        try {
            signUpLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    signUpLiveDate.postValue(
                        Resources.success(
                            ApiRepository().signupApi(request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    signUpLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun onSubmit() {
        val emailInput = email.get()?.trim()
        val nameInput = name.get()?.trim()
        val phoneInput = phone.get()?.trim()
        val passwordInput = password.get()?.trim()

        if (nameInput.isNullOrEmpty()) {
            _validationMessage.value = "Please Enter Name"
            return
        }
        if (emailInput.isNullOrEmpty()) {
            _validationMessage.value = "Please Enter Email"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            _validationMessage.value = "Please Enter Valid Email"
            return
        }
        if (phoneInput.isNullOrEmpty()) {
            _validationMessage.value = "Please Enter Phone"
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

//        val deviceToken = Preferences.getStringPreference(getApplication(), FCM_TOKEN)
        val deviceToken = "test"
        val userType = Preferences.getStringPreference(getApplication(), USER_TYPE)
        val deviceId = "test"
        var request = LoginRequest(name = nameInput, email = emailInput, mobile = phoneInput, password = passwordInput, device_token = deviceToken, device_id = deviceId, device_type = "android", user_type = userType)
        UserPreference.loginRequest = request
        hitSignUpDataApi(request)
//        _validationMessage.value = "Successful!"
    }


}