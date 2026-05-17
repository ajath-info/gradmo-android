package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstituteDetailsViewModel @Inject constructor() : ViewModel() {
    private val institutesLiveDate = SingleLiveEvent<Resources<InstituteDetailResponse>>()

    fun getInstitutesLiveData(): LiveData<Resources<InstituteDetailResponse>> {
        return institutesLiveDate
    }
    fun hitInstitutesDataApi(token: String, request: InstituteDetailRequest) {

        try {
            institutesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    institutesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getInstituteDetailApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    institutesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}