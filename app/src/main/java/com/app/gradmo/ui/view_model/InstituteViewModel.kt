package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.user_detail.UserDetailResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstituteViewModel @Inject constructor() : ViewModel() {
    private val userDetailsLiveData = SingleLiveEvent<Resources<UserDetailResponse>>()

    fun getInstitutesLiveData(): LiveData<Resources<UserDetailResponse>> {
        return userDetailsLiveData
    }
    fun hitInstitutesDataApi(token: String) {

        try {
            userDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    userDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getUserDetailsApi(token
                            )
                        )
                    )
                } catch (ex: Exception) {
                    userDetailsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}