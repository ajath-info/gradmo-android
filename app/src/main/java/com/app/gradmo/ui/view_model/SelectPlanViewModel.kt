package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.plan_detail.PlanDetailsResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
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