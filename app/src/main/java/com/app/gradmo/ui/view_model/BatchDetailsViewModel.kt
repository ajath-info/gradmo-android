package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.batch_detail.BatchDetailResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
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