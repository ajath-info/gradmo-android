package com.app.edtech.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.ui.signup.model.ChangePasswordRequest
import com.app.edtech.ui.signup.model.ResetPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val resetPasswordLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getResetPasswordLiveData(): LiveData<Resources<LoginResponse>> {
        return resetPasswordLiveDate
    }

    fun hitResetPassword(model: ResetPasswordRequest) {

        try {
            resetPasswordLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    resetPasswordLiveDate.postValue(
                        Resources.success(
                            ApiRepository().resetPassword(model)
                        )
                    )


                } catch (ex: Exception) {
                    resetPasswordLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val changePasswordLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getChangePasswordLiveDataLiveData(): LiveData<Resources<LoginResponse>> {
        return changePasswordLiveDate
    }

    fun hitChangePassword(token:String,model: ChangePasswordRequest) {

        try {
            changePasswordLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    changePasswordLiveDate.postValue(
                        Resources.success(
                            ApiRepository().changePassword(token,model)
                        )
                    )


                } catch (ex: Exception) {
                    changePasswordLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

