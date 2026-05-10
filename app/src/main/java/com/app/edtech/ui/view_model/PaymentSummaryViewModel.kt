package com.app.edtech.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edtech.model.create_order.CreateOrderResponse
import com.app.edtech.model.institute_detail.request.InstituteDetailRequest
import com.app.edtech.model.institute_detail.response.InstituteDetailResponse
import com.app.edtech.model.verify_payment.VerifyPaymentRequest
import com.app.edtech.model.verify_payment.VerifyPaymentResponse
import com.app.edtech.network_call.repository.ApiRepository
import com.app.edtech.utils.network_utils.Resources
import com.app.edtech.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSummaryViewModel @Inject constructor() : ViewModel() {
    private val createOrderLiveDate = SingleLiveEvent<Resources<CreateOrderResponse>>()

    fun getCreateOrderLiveData(): LiveData<Resources<CreateOrderResponse>> {
        return createOrderLiveDate
    }
    fun hitCreateOrderDataApi(token: String, amount_in_rupees: String) {

        try {
            createOrderLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    createOrderLiveDate.postValue(
                        Resources.success(
                            ApiRepository().createOrderApi(token, amount_in_rupees)
                        )
                    )
                } catch (ex: Exception) {
                    createOrderLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }



    private val verifyPaymentLiveDate = SingleLiveEvent<Resources<VerifyPaymentResponse>>()

    fun getVerifyPaymentLiveData(): LiveData<Resources<VerifyPaymentResponse>> {
        return verifyPaymentLiveDate
    }
    fun hitVerifyPaymentDataApi(token: String, request: VerifyPaymentRequest) {

        try {
            verifyPaymentLiveDate.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    verifyPaymentLiveDate.postValue(
                        Resources.success(
                            ApiRepository().verifyPaymentApi(token, request)
                        )
                    )
                } catch (ex: Exception) {
                    verifyPaymentLiveDate.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}