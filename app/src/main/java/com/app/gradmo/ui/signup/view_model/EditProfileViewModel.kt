package com.app.gradmo.ui.signup.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.city.GetCitiesResponse
import com.app.gradmo.model.address.state.GetStatesRequest
import com.app.gradmo.model.address.state.GetStatesResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
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


    private val citiesLiveDate = SingleLiveEvent<Resources<GetCitiesResponse>>()

    fun getCitiesLiveData(): LiveData<Resources<GetCitiesResponse>> {
        return citiesLiveDate
    }
    fun hitCitiesDataApi(request: GetCitiesRequest) {

        try {
            citiesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    citiesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getCitiesApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    citiesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }




    private val statesLiveDate = SingleLiveEvent<Resources<GetStatesResponse>>()

    fun getStatesLiveData(): LiveData<Resources<GetStatesResponse>> {
        return statesLiveDate
    }
    fun hitStatesDataApi(request: GetStatesRequest) {

        try {
            statesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    statesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getStatesApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    statesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}