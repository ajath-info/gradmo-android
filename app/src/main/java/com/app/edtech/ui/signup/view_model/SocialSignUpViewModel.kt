package com.app.edtech.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import com.app.hihlo.model.check_username.request.CheckUsernameRequest
import com.app.hihlo.model.check_username.response.CheckUsernameResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.hihlo.ui.signup.model.SocialSignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialSignUpViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val SocialSignUpLiveDate = SingleLiveEvent<Resources<LoginResponse>>()

    fun getSocialSignUpLiveData(): LiveData<Resources<LoginResponse>> {
        return SocialSignUpLiveDate
    }

    fun hitSocialSignUpUser(model: SocialSignUpRequest) {

        try {
            SocialSignUpLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    SocialSignUpLiveDate.postValue(
                        Resources.success(
                            ApiRepository().socialLogin(model)
                        )
                    )


                } catch (ex: Exception) {
                    SocialSignUpLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private val checkUsernameLiveData = SingleLiveEvent<Resources<CheckUsernameResponse>>()

    fun getCheckUsernameLiveData(): LiveData<Resources<CheckUsernameResponse>> {
        return checkUsernameLiveData
    }
    fun hitCheckUsernameDataApi(username: String) {

        try {
            checkUsernameLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    checkUsernameLiveData.postValue(
                        Resources.success(
                            ApiRepository().checkUsernameApi(CheckUsernameRequest(username))
                        )
                    )
                } catch (ex: Exception) {
                    checkUsernameLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}