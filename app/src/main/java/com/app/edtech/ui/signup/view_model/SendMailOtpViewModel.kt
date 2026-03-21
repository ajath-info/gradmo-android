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
class SendMailOtpViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val loginLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getLoginLiveData(): LiveData<Resources<LoginResponse>> {
        return loginLiveDate
    }

    fun hitSendEmailOtp(email:String,userName:String?,purpose: String?) {

        try {
            loginLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    loginLiveDate.postValue(
                        Resources.success(
                            ApiRepository().sendEmailOtp(email,userName,purpose)
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
