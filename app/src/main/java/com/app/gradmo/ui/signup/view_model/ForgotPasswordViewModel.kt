package com.app.gradmo.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    private val forgotPasswordSendOtpLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getForgotPasswordSendOtpLiveData(): LiveData<Resources<LoginResponse>> {
        return forgotPasswordSendOtpLiveDate
    }

    fun hitForgotPasswordSendOtp(request: LoginRequest) {
        try {
            forgotPasswordSendOtpLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    forgotPasswordSendOtpLiveDate.postValue(
                        Resources.success(
                            ApiRepository().sendLoginOtpApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    forgotPasswordSendOtpLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}