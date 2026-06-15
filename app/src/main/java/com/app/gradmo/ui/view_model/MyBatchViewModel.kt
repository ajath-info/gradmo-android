package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.batch_list.BatchListResponse
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.third_party_credentials.ThirdPartyCredentialsResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBatchViewModel @Inject constructor() : ViewModel() {
    private val batchesLiveDate = SingleLiveEvent<Resources<BatchListResponse>>()

    fun getBatchesLiveData(): LiveData<Resources<BatchListResponse>> {
        return batchesLiveDate
    }
    fun hitBatchesDataApi(token: String, request: BatchListRequest) {

        try {
            batchesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    batchesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getBatchListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    batchesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}