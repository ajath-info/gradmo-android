package com.app.edtech.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.profile.request.UpdateProfileRequest
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    private val updateProfileLiveData = SingleLiveEvent<Resources<LoginResponse>>()

    fun getUpdateProfileLiveData(): LiveData<Resources<LoginResponse>> {
        return updateProfileLiveData
    }

    fun hitUpdateProfile(
        image: MultipartBody.Part?,
        requestMap: Map<String, RequestBody>,
        accessToken: String
    ) {
        updateProfileLiveData.postValue(Resources.loading(null))

        viewModelScope.launch {
            try {
                val response = ApiRepository().updateProfileApi(image, requestMap, accessToken)
                updateProfileLiveData.postValue(Resources.success(response))
            } catch (ex: Exception) {
                updateProfileLiveData.postValue(Resources.error(ex.localizedMessage, null))
            }
        }
    }

}