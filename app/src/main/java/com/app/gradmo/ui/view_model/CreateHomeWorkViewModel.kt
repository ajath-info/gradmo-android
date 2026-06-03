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
class CreateHomeWorkViewModel @Inject constructor() : ViewModel() {
    private val createHomeworkLiveData = SingleLiveEvent<Resources<BatchDetailResponse>>()

    fun getCreateHomeWorkLiveData(): LiveData<Resources<BatchDetailResponse>> {
        return createHomeworkLiveData
    }
    fun hitCreateHomeWorkApi(token: String, batch_id: String) {
        try {
            createHomeworkLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    createHomeworkLiveData.postValue(
                        Resources.success(
                            ApiRepository().batchDetailsApi(token, batch_id
                            )
                        )
                    )
                } catch (ex: Exception) {
                    createHomeworkLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}