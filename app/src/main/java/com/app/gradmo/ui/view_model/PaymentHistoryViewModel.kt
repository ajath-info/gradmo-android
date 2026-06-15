package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.payment_history.PaymentHistoryRequest
import com.app.gradmo.model.payment_history.PaymentHistoryResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor() : ViewModel(){

    private val paymentHistoryLiveData = SingleLiveEvent<Resources<PaymentHistoryResponse>>()

    fun getPaymentHistoryLiveData(): LiveData<Resources<PaymentHistoryResponse>> {
        return paymentHistoryLiveData
    }
    fun hitPaymentHistoryApi(token: String, request: PaymentHistoryRequest) {

        try {
            paymentHistoryLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    paymentHistoryLiveData.postValue(
                        Resources.success(
                            ApiRepository().getPaymentHistoryApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    paymentHistoryLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}