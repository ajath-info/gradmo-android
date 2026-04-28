package com.app.edtech.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edtech.model.address.city.GetCitiesRequest
import com.app.edtech.model.address.city.GetCitiesResponse
import com.app.edtech.model.banner.response.BannerResponse
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.plan_detail.PlanDetailsResponse
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectPlanViewModel @Inject constructor() : ViewModel() {
    private val planDetailLiveDate = SingleLiveEvent<Resources<PlanDetailsResponse>>()

    fun getPlanDetailLiveData(): LiveData<Resources<PlanDetailsResponse>> {
        return planDetailLiveDate
    }
    fun hitPlanDetailApi(token: String, batchId: String) {

        try {
            planDetailLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    planDetailLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getPlanDetailsApi(token, batchId
                            )
                        )
                    )
                } catch (ex: Exception) {
                    planDetailLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}