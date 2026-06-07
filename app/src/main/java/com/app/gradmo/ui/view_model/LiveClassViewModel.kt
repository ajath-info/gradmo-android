package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.create_zoom.CreateZoomRequest
import com.app.gradmo.model.create_zoom.CreateZoomResponse
import com.app.gradmo.model.end_zoom.EndZoomClassRequest
import com.app.gradmo.model.end_zoom.EndZoomClassResponse
import com.app.gradmo.model.live_class.BatchLiveClassListRequest
import com.app.gradmo.model.live_class.BatchLiveClassListResponse
import com.app.gradmo.model.live_class.LiveClassDetailsRequest
import com.app.gradmo.model.live_class.LiveClassDetailsResponse
import com.app.gradmo.model.zoom_details.ZoomDetailsResponse
import com.app.gradmo.network_call.repository.ApiRepository
import com.app.gradmo.utils.network_utils.Resources
import com.app.gradmo.utils.network_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveClassViewModel @Inject constructor() : ViewModel() {
//    private val liveClassListLiveData = SingleLiveEvent<Resources<BatchLiveClassListResponse>>()
//
//    fun getLiveClassListLiveData(): LiveData<Resources<BatchLiveClassListResponse>> {
//        return liveClassListLiveData
//    }
//    fun hitLiveClassListApi(token: String, request: BatchLiveClassListRequest) {
//
//        try {
//            liveClassListLiveData.postValue(Resources.loading(null))
//            viewModelScope.launch {
//                try {
//                    liveClassListLiveData.postValue(
//                        Resources.success(
//                            ApiRepository().getLiveClassListApi(token, request
//                            )
//                        )
//                    )
//                } catch (ex: Exception) {
//                    liveClassListLiveData.postValue(Resources.error(ex.localizedMessage, null))
//
//                }
//            }
//
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }

    private val liveClassDetailsLiveData = SingleLiveEvent<Resources<LiveClassDetailsResponse>>()

    fun getLiveClassDetailsLiveData(): LiveData<Resources<LiveClassDetailsResponse>> {
        return liveClassDetailsLiveData
    }
    fun hitLiveClassDetailsApi(token: String, request: LiveClassDetailsRequest) {

        try {
            liveClassDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    liveClassDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getLiveClassDetailsApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    liveClassDetailsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private val getZoomDetailsLiveData = SingleLiveEvent<Resources<ZoomDetailsResponse>>()

    fun getZoomDetailsLiveData(): LiveData<Resources<ZoomDetailsResponse>> {
        return getZoomDetailsLiveData
    }
    fun hitZoomDetailsApi(token: String, batchId: String) {

        try {
            getZoomDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getZoomDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getZoomDetailsApi(token, batchId
                            )
                        )
                    )
                } catch (ex: Exception) {
                    getZoomDetailsLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private val createZoomClassLiveData = SingleLiveEvent<Resources<CreateZoomResponse>>()

    fun getCreateZoomLiveData(): LiveData<Resources<CreateZoomResponse>> {
        return createZoomClassLiveData
    }
    fun hitCreateZoomApi(token: String, request: CreateZoomRequest) {

        try {
            createZoomClassLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    createZoomClassLiveData.postValue(
                        Resources.success(
                            ApiRepository().createZoomClassApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    createZoomClassLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }



    private val endZoomClassLiveData = SingleLiveEvent<Resources<EndZoomClassResponse>>()

    fun getEndZoomLiveData(): LiveData<Resources<EndZoomClassResponse>> {
        return endZoomClassLiveData
    }
    fun hitEndZoomApi(token: String, request: EndZoomClassRequest) {

        try {
            endZoomClassLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    endZoomClassLiveData.postValue(
                        Resources.success(
                            ApiRepository().endZoomClassApi(token, request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    endZoomClassLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}