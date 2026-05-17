package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.promocode.response.PromocodeListResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromocodeListViewModel @Inject constructor() : ViewModel() {
    private val promoCodesLiveDate = SingleLiveEvent<Resources<PromocodeListResponse>>()

    fun getPromoCodesLiveData(): LiveData<Resources<PromocodeListResponse>> {
        return promoCodesLiveDate
    }
    fun hitPromoCodesApi(token: String, batchId: String) {

        try {
            promoCodesLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    promoCodesLiveDate.postValue(
                        Resources.success(
                            ApiRepository().getPromocodesApi(token, batchId
                            )
                        )
                    )
                } catch (ex: Exception) {
                    promoCodesLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}