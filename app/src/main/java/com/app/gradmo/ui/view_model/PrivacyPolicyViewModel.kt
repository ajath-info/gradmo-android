package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.content_page.ContentPagesResponse
import com.app.gradmo.model.plan_detail.PlanDetailsResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentDataViewModel @Inject constructor() : ViewModel() {
    private val contentDataLiveDate = SingleLiveEvent<Resources<ContentPagesResponse>>()

    fun getContentDataLiveData(): LiveData<Resources<ContentPagesResponse>> {
        return contentDataLiveDate
    }
    fun hitContentDataApi(token: String) {

        try {
            contentDataLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    contentDataLiveDate.postValue(
                        Resources.success(
                            ApiRepository().contentPageDataApi(token
                            )
                        )
                    )
                } catch (ex: Exception) {
                    contentDataLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}