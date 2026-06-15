package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.notification_list.NotificationListRequest
import com.app.gradmo.model.notification_list.NotificationListResponse
import com.app.gradmo.model.payment_history.PaymentHistoryRequest
import com.app.gradmo.model.payment_history.PaymentHistoryResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(): ViewModel() {
    private val notificationListLiveData = SingleLiveEvent<Resources<NotificationListResponse>>()

    fun getNotificationListLiveData(): LiveData<Resources<NotificationListResponse>> {
        return notificationListLiveData
    }
    fun hitNotificationListApi(token: String, request: NotificationListRequest) {

        try {
            notificationListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    notificationListLiveData.postValue(
                        Resources.success(
                            ApiRepository().getNotificationListApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    notificationListLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}