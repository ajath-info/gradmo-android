package com.app.edtech.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edtech.model.address.city.GetCitiesRequest
import com.app.edtech.model.address.city.GetCitiesResponse
import com.app.edtech.model.banner.response.BannerResponse
import com.app.edtech.model.batch_detail.BatchDetailResponse
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatchDetailsViewModel @Inject constructor() : ViewModel() {
    private val batchDetailsLiveData = SingleLiveEvent<Resources<BatchDetailResponse>>()

    fun getBatchDetailsLiveData(): LiveData<Resources<BatchDetailResponse>> {
        return batchDetailsLiveData
    }
    fun hitBatchDetailsApi(token: String, batch_id: String) {
        try {
            batchDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    batchDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().batchDetailsApi(token, batch_id
                            )
                        )
                    )
                } catch (ex: Exception) {
                    batchDetailsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}