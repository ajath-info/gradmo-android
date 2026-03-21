package com.app.edtech.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import com.app.hihlo.model.login.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VerifyEmailOtpViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val verifyLiveData = SingleLiveEvent<Resources<LoginResponse>>()

    fun getLoginLiveData(): LiveData<Resources<LoginResponse>> {
        return verifyLiveData
    }

    fun hitVerifyEmailOtp(email:String,otp:String) {

        try {
            verifyLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    verifyLiveData.postValue(
                        Resources.success(
                            ApiRepository().verifyEmailOtp(email,otp)
                        )
                    )

                } catch (ex: Exception) {
                    verifyLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}