package com.app.gradmo.ui.signup.view_model
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import com.app.gradmo.model.login.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SendMailOtpViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val verifyOtpLiveData = SingleLiveEvent<Resources<LoginResponse>>()

    fun getVerifyOtpLiveData(): LiveData<Resources<LoginResponse>> {
        return verifyOtpLiveData
    }

    fun hitVerifyOtp(mobile:String,otp:String,userType: String) {

        try {
            verifyOtpLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    verifyOtpLiveData.postValue(
                        Resources.success(
                            ApiRepository().verifyLoginOtp(mobile,otp,userType)
                        )
                    )


                } catch (ex: Exception) {
                    verifyOtpLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }




    private val resendLoginOtpLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getResendLoginOtpLiveData(): LiveData<Resources<LoginResponse>> {
        return resendLoginOtpLiveDate
    }

    fun hitResendLoginOtp(request: LoginRequest) {

        try {
            resendLoginOtpLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    resendLoginOtpLiveDate.postValue(
                        Resources.success(
                            ApiRepository().sendLoginOtpApi(request)
                        )
                    )


                } catch (ex: Exception) {
                    resendLoginOtpLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}
