package com.app.gradmo.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.logout.response.DeleteResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val logoutLiveData = SingleLiveEvent<Resources<LoginResponse>>()
    fun getLogoutLiveData(): LiveData<Resources<LoginResponse>> = logoutLiveData

    private val deleteAccountLiveData = SingleLiveEvent<Resources<DeleteResponse>>()
    fun getDeleteAccountLiveData(): LiveData<Resources<DeleteResponse>> = deleteAccountLiveData

    fun hitLogoutApi(token: String, studentId: String) {
        logoutLiveData.postValue(Resources.loading(null))

        viewModelScope.launch {
            try {
                val response = ApiRepository().logoutApi(token, studentId)
                logoutLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                logoutLiveData.postValue(Resources.error(e.localizedMessage, null))
            }
        }
    }

    fun hitDeleteAccountApi(token: String, studentId: String) {
        deleteAccountLiveData.postValue(Resources.loading(null))

        viewModelScope.launch {
            try {
                val response = ApiRepository().deleteApi(token, studentId)
                deleteAccountLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                deleteAccountLiveData.postValue(Resources.error(e.localizedMessage, null))
            }
        }
    }
}