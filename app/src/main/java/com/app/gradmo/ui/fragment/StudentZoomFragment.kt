package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.BuildConfig
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentStudentZoomBinding
import com.app.gradmo.databinding.FragmentTeacherZoomBinding
import com.app.gradmo.model.create_zoom.CreateZoomRequest
import com.app.gradmo.model.end_zoom.EndZoomClassRequest
import com.app.gradmo.model.live_class.LiveClassDetailsRequest
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.zoom_details.ZoomDetailsResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.view_model.LiveClassViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import dagger.hilt.android.AndroidEntryPoint
import us.zoom.sdk.JoinMeetingOptions
import us.zoom.sdk.JoinMeetingParam4WithoutLogin
import us.zoom.sdk.MeetingError
import us.zoom.sdk.MeetingParameter
import us.zoom.sdk.MeetingService
import us.zoom.sdk.MeetingServiceListener
import us.zoom.sdk.MeetingStatus
import us.zoom.sdk.StartMeetingOptions
import us.zoom.sdk.StartMeetingParamsWithoutLogin
import us.zoom.sdk.ZoomError
import us.zoom.sdk.ZoomSDK
import us.zoom.sdk.ZoomSDKInitParams
import us.zoom.sdk.ZoomSDKInitializeListener
import kotlin.getValue

@AndroidEntryPoint
class StudentZoomFragment : BaseFragment<FragmentStudentZoomBinding>() {

    var batch_id: String = ""
    var batchName: String = ""
    var zoomMeetingId: String = ""
    private val viewModel: LiveClassViewModel by viewModels()
    var accessToken = ""

    // Credentials from liveClassDetailsApi
    private var sdkJwtToken: String = ""   // the full JWT signature from meeting.signature
    private var meetingNumber: String = ""  // meeting.meetingNumber
    private var meetingPassword: String = "" // meeting.password
    private var zak: String = ""            // meeting.zak
    private var displayName: String = ""    // meeting.displayName

    override fun initView(savedInstanceState: Bundle?) {
        batch_id = arguments?.getString("batch_id") ?: ""
        batchName = arguments?.getString("batchName") ?: ""
        binding.batchName.text = batchName
        accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.accessToken ?: ""
    }

    override fun onResume() {
        super.onResume()
        viewModel.hitLiveClassDetailsApi("Bearer $accessToken", LiveClassDetailsRequest(batch_id = batch_id, live_class_id = "0"))
        viewModel.hitZoomDetailsApi("Bearer $accessToken", batch_id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    private fun initSDKAndStartMeeting() {
        if (sdkJwtToken.isEmpty() || meetingNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Meeting credentials not ready", Toast.LENGTH_SHORT).show()
            return
        }
        ProcessDialog.showDialog(requireContext(), true)

        val params = ZoomSDKInitParams().apply {
            jwtToken  = sdkJwtToken
            domain    = "zoom.us"
            enableLog = BuildConfig.DEBUG
        }

        ZoomSDK.getInstance().initialize(
            requireActivity(),
            object : ZoomSDKInitializeListener {
                override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                    if (errorCode == ZoomError.ZOOM_ERROR_SUCCESS ||
                        errorCode == ZoomError.ZOOM_ERROR_ILLEGAL_APP_KEY_OR_SECRET) {
                        joinAfterInit()   // ← join, not start
                    } else {
                        ProcessDialog.dismissDialog(true)
                        Log.e("Zoom", "SDK init failed: $errorCode")
                        Toast.makeText(requireContext(), "Zoom init failed: $errorCode", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onZoomAuthIdentityExpired() {
                    ProcessDialog.dismissDialog(true)
                }
            },
            params
        )
    }

    // Called after SDK is initialized — student joins as attendee
    private fun joinAfterInit() {
        val sdk = ZoomSDK.getInstance()
        if (!sdk.isInitialized) {
            ProcessDialog.dismissDialog(true)
            Toast.makeText(requireContext(), "Zoom SDK not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        val meetingService = sdk.meetingService ?: run {
            ProcessDialog.dismissDialog(true)
            Toast.makeText(requireContext(), "Meeting service unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        meetingService.addListener(zoomMeetingListener)

        val options = JoinMeetingOptions().apply {
            no_driving_mode = true
            no_invite       = true
        }

        val joinParams = JoinMeetingParam4WithoutLogin().apply {
            meetingNo       = meetingNumber
            displayName     = this@StudentZoomFragment.displayName
            password        = meetingPassword
            // students don't need ZAK — that's only for hosts
            userType        = MeetingService.USER_TYPE_API_USER
        }

        val ret = meetingService.joinMeetingWithParams(requireContext(), joinParams, options)
        Log.d("Zoom", "joinMeetingWithParams result: $ret")

        if (ret != MeetingError.MEETING_ERROR_SUCCESS) {
            ProcessDialog.dismissDialog(true)
            Toast.makeText(requireContext(), "Failed to join: error $ret", Toast.LENGTH_SHORT).show()
        }
    }

    private fun joinExistingMeeting() {
        val sdk = ZoomSDK.getInstance()
        if (!sdk.isInitialized) {
            initSDKAndStartMeeting()  // init first, then joinAfterInit() is called inside
            return
        }

        ProcessDialog.showDialog(requireContext(), true)

        val meetingService = sdk.meetingService ?: run {
            ProcessDialog.dismissDialog(true)
            return
        }

        meetingService.addListener(zoomMeetingListener)

        val options = JoinMeetingOptions().apply {
            no_driving_mode = true
            no_invite       = true
        }

        val joinParams = JoinMeetingParam4WithoutLogin().apply {
            meetingNo       = meetingNumber
            displayName     = this@StudentZoomFragment.displayName
            password        = meetingPassword
            userType        = MeetingService.USER_TYPE_API_USER
        }

        val ret = meetingService.joinMeetingWithParams(requireContext(), joinParams, options)
        Log.d("Zoom", "joinMeetingWithParams result: $ret")

        if (ret != MeetingError.MEETING_ERROR_SUCCESS) {
            ProcessDialog.dismissDialog(true)
            Toast.makeText(requireContext(), "Failed to join: error $ret", Toast.LENGTH_SHORT).show()
        }
    }

    // Meeting listener — student doesn't call endZoomApi on end
    private val zoomMeetingListener = object : MeetingServiceListener {
        override fun onMeetingStatusChanged(
            meetingStatus: MeetingStatus?,
            errorCode: Int,
            internalErrorCode: Int
        ) {
            Log.d("Zoom", "Meeting status: $meetingStatus")
            when (meetingStatus) {
                MeetingStatus.MEETING_STATUS_INMEETING -> {
                    ProcessDialog.dismissDialog(true)
                }
                MeetingStatus.MEETING_STATUS_ENDED -> {
                    // Host ended the class — just go back
                    ProcessDialog.dismissDialog(true)
                    findNavController().popBackStack()
                }
                MeetingStatus.MEETING_STATUS_FAILED -> {
                    ProcessDialog.dismissDialog(true)
                    Toast.makeText(requireContext(), "Meeting failed: $errorCode", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
        override fun onMeetingParameterNotification(p0: MeetingParameter?) {}
    }

    // ─────────────────────────────────────────────────────────────
    // 6. Observers
    // ─────────────────────────────────────────────────────────────
    private fun setObserver() {

        // zoomDetails → populate UI
        viewModel.getZoomDetailsLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    if (it.data?.status == "true") {
                        zoomMeetingId = it.data.data.zoom.zoomMeetingId ?: ""
                        setupUI(it.data.status, it.data.data.zoom)
                    } else {
                        setupUI(
                            it.data?.status.toString(),
                            it.data?.data?.zoom ?: ZoomDetailsResponse.Data.Zoom()
                        )
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("Zoom", "zoomDetails failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        // liveClassDetails → store all SDK credentials here
        viewModel.getLiveClassDetailsLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    if (it.data?.status == "true") {
                        it.data.liveClass?.meeting?.let { m ->
                            sdkJwtToken     = m.signature    ?: ""  // full JWT from API
                            meetingNumber   = m.meetingNumber ?: ""
                            meetingPassword = m.password     ?: ""
                            zak             = m.zak          ?: ""
                            displayName     = m.displayName  ?: "Teacher"
                        }
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("Zoom", "liveClassDetails failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 7. UI
    // ─────────────────────────────────────────────────────────────
    private fun setupUI(status: String, zoom: ZoomDetailsResponse.Data.Zoom) {
        binding.apply {
            if (status == "true") {
                joinClass.isVisible    = true
                meetingId.isVisible     = true
                meetingId.text = zoom.zoomMeetingId
                classStatus.text        = "Live Class Running"
            } else {
                classStatus.text        = "No Live Class Running"
                joinClass.isVisible    = false
                meetingId.isVisible     = false
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 8. Click events
    // ─────────────────────────────────────────────────────────────
    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Start Class → create on server, then launch SDK in observer
        binding.joinClass.setOnClickListener {
            joinExistingMeeting()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 9. Cleanup
    // ─────────────────────────────────────────────────────────────
    override fun onDestroyView() {
        super.onDestroyView()
        ZoomSDK.getInstance().meetingService?.removeListener(zoomMeetingListener)
    }

    override fun getLayoutId(): Int = R.layout.fragment_student_zoom
    override fun restoreView() {}
}