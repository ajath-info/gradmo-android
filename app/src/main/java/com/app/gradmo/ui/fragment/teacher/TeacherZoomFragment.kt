package com.app.gradmo.ui.fragment.teacher

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentTeacherZoomBinding
import com.app.gradmo.model.create_zoom.CreateZoomRequest
import com.app.gradmo.model.end_zoom.EndZoomClassRequest
import com.app.gradmo.model.live_class.LiveClassDetailsRequest
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.zoom_details.ZoomDetailsResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.ZoomVideoActivity
import com.app.gradmo.ui.view_model.LiveClassViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import us.zoom.sdk.ZoomSDK
import us.zoom.sdk.ZoomSDKInitParams
import us.zoom.sdk.MeetingService
import us.zoom.sdk.MeetingServiceListener
import us.zoom.sdk.MeetingStatus
import us.zoom.sdk.StartMeetingParams4NormalUser
import us.zoom.sdk.JoinMeetingOptions
import us.zoom.sdk.JoinMeetingParam4WithoutLogin
import us.zoom.sdk.MeetingError
import us.zoom.sdk.MeetingParameter
import us.zoom.sdk.MeetingViewsOptions
import us.zoom.sdk.StartMeetingOptions
import us.zoom.sdk.ZoomError
import us.zoom.sdk.ZoomSDKInitializeListener
import us.zoom.sdk.StartMeetingParamsWithoutLogin
import com.app.gradmo.BuildConfig

@AndroidEntryPoint
class TeacherZoomFragment : BaseFragment<FragmentTeacherZoomBinding>() {

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
//        initZoomSDK()
        setObserver()
        clickEvent()
    }

    // ─────────────────────────────────────────────────────────────
    // 1. Init SDK — uses jwtToken (not appKey/appSecret)
    // ─────────────────────────────────────────────────────────────
    private fun initZoomSDK() {
        val params = ZoomSDKInitParams().apply {
            jwtToken  = sdkJwtToken   // set later once API responds; re-init if needed
            domain    = "zoom.us"
            enableLog = BuildConfig.DEBUG
        }
        ZoomSDK.getInstance().initialize(
            requireActivity(),
            object : ZoomSDKInitializeListener {
                override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                    if (errorCode == ZoomError.ZOOM_ERROR_SUCCESS) {
                        Log.d("Zoom", "SDK initialized successfully")
                    } else {
                        Log.e("Zoom", "SDK init failed: $errorCode / $internalErrorCode")
                        Toast.makeText(requireContext(), "Zoom SDK init failed: $errorCode", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onZoomAuthIdentityExpired() {
                    Log.w("Zoom", "Auth identity expired")
                }
            },
            params
        )
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Re-init with JWT then start — called after we have credentials
    // ─────────────────────────────────────────────────────────────
    private fun initSDKAndStartMeeting() {
        if (sdkJwtToken.isEmpty() || meetingNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Meeting credentials not ready", Toast.LENGTH_SHORT).show()
            return
        }
        ProcessDialog.showDialog(requireContext(), true)  // ← show loader here

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
                        // ZOOM_ERROR_ILLEGAL_APP_KEY_OR_SECRET here just means already initialized
                        startMeetingAsHost()
                    } else {
                        ProcessDialog.dismissDialog(true)  // ← dismiss on failure
                        Log.e("Zoom", "SDK init failed: $errorCode")
                        Toast.makeText(requireContext(), "Zoom init failed: $errorCode", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onZoomAuthIdentityExpired() {
                    ProcessDialog.dismissDialog(true)  // ← dismiss on expiry
                }
            },
            params
        )
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Start meeting as host using StartMeetingParamsWithoutLogin
    //    Fields available: userType, zoomAccessToken, displayName (from class)
    //                    + meetingNo (from parent StartMeetingParams)
    // ─────────────────────────────────────────────────────────────
    private fun startMeetingAsHost() {
        val sdk = ZoomSDK.getInstance()
        if (!sdk.isInitialized) {
            ProcessDialog.dismissDialog(true)  // ← dismiss if SDK not ready
            Toast.makeText(requireContext(), "Zoom SDK not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        val meetingService = sdk.meetingService ?: run {
            ProcessDialog.dismissDialog(true)  // ← dismiss if SDK not ready
            Toast.makeText(requireContext(), "Meeting service unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        meetingService.addListener(zoomMeetingListener)

        val options = StartMeetingOptions().apply {
            no_driving_mode = true
            no_invite       = true
        }

        // StartMeetingParamsWithoutLogin fields:
        //   from parent StartMeetingParams → meetingNo
        //   own fields → userType, zoomAccessToken, displayName
        // NOTE: password and token/signature are NOT fields here —
        //       for host start, zoomAccessToken (ZAK) is the auth
        val startParams = StartMeetingParamsWithoutLogin().apply {
            meetingNo       = meetingNumber
            displayName     = this@TeacherZoomFragment.displayName
            zoomAccessToken = zak              // ZAK token — host auth
            userType        = MeetingService.USER_TYPE_API_USER
        }

        val ret = meetingService.startMeetingWithParams(requireContext(), startParams, options)
        Log.d("Zoom", "startMeetingWithParams result: $ret")

        if (ret != MeetingError.MEETING_ERROR_SUCCESS) {
            ProcessDialog.dismissDialog(true)  // ← dismiss if SDK not ready
            Toast.makeText(requireContext(), "Failed to start: error $ret", Toast.LENGTH_SHORT).show()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Join existing meeting (continueClass button)
    //    Uses JoinMeetingParam4WithoutLogin which has password in parent
    // ─────────────────────────────────────────────────────────────
    private fun joinExistingMeeting() {
        val sdk = ZoomSDK.getInstance()
        if (!sdk.isInitialized) {
            initSDKAndStartMeeting()  // re-init then start
            return
        }
        ProcessDialog.showDialog(requireContext(), true)  // ← show loader here for already-initialized case
        val meetingService = sdk.meetingService ?: return
        meetingService.addListener(zoomMeetingListener)

        val options = JoinMeetingOptions().apply {
            no_driving_mode = true
            no_invite       = true
        }

        // JoinMeetingParam4WithoutLogin fields:
        //   from parent JoinMeetingParams → meetingNo, displayName, password
        //   own fields → zoomAccessToken, userType
        val joinParams = JoinMeetingParam4WithoutLogin().apply {
            meetingNo       = meetingNumber
            displayName     = this@TeacherZoomFragment.displayName
            password        = meetingPassword
            zoomAccessToken = zak
            userType        = MeetingService.USER_TYPE_API_USER
        }

        val ret = meetingService.joinMeetingWithParams(requireContext(), joinParams, options)
        Log.d("Zoom", "joinMeetingWithParams result: $ret")

        if (ret != MeetingError.MEETING_ERROR_SUCCESS) {
            ProcessDialog.dismissDialog(true)  // ← dismiss on join failure
            Toast.makeText(requireContext(), "Failed to join: error $ret", Toast.LENGTH_SHORT).show()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. Meeting event listener
    // ─────────────────────────────────────────────────────────────
    private val zoomMeetingListener = object : MeetingServiceListener {
        override fun onMeetingStatusChanged(
            meetingStatus: MeetingStatus?,
            errorCode: Int,
            internalErrorCode: Int
        ) {
            Log.d("Zoom", "Meeting status: $meetingStatus")
            when (meetingStatus) {
                MeetingStatus.MEETING_STATUS_INMEETING -> {
                    ProcessDialog.dismissDialog(true)  // ← dismiss when fully in meeting
                }
                MeetingStatus.MEETING_STATUS_ENDED -> {
                    Log.d("Zoom", "Meeting ended — calling endZoomApi")
                    viewModel.hitEndZoomApi(
                        "Bearer $accessToken",
                        EndZoomClassRequest(batch_id = batch_id)
                    )
                }
                MeetingStatus.MEETING_STATUS_FAILED -> {
                    ProcessDialog.dismissDialog(true)  // ← dismiss on failure
                    Toast.makeText(
                        requireContext(),
                        "Meeting failed: $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
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

        // createZoom success → init SDK and start meeting
        viewModel.getCreateZoomLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    if (it.data?.status == "true") {
//                        initSDKAndStartMeeting()   // ← credentials already stored from liveClassDetails
                        viewModel.hitLiveClassDetailsApi("Bearer $accessToken", LiveClassDetailsRequest(batch_id = batch_id, live_class_id = "0"))
                        viewModel.hitZoomDetailsApi("Bearer $accessToken", batch_id)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("Zoom", "createZoom failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

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

        // endZoom → pop back
        viewModel.getEndZoomLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    if (it.data?.status == true) {
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("Zoom", "endZoom failed: ${it.message}")
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
                startClass.isVisible    = false
                meetingId.isVisible     = true
                meetingId.text = zoom.zoomMeetingId
                classStatus.text        = "Continue to Join the Class"
                continueClass.isVisible = true
                endClass.isVisible      = true
            } else {
                classStatus.text        = "No Live Class Running"
                startClass.isVisible    = true
                meetingId.isVisible     = false
                continueClass.isVisible = false
                endClass.isVisible      = false
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
        binding.startClass.setOnClickListener {
            viewModel.hitCreateZoomApi(
                "Bearer $accessToken",
                CreateZoomRequest(batch_id = batch_id, topic = batchName)
            )
        }

        // Continue Class → rejoin existing meeting directly
        binding.continueClass.setOnClickListener {
            joinExistingMeeting()
        }

        // End Class button in your own UI
        binding.endClass.setOnClickListener {
            val meetingService = ZoomSDK.getInstance().meetingService
            if (meetingService?.meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
                meetingService.leaveCurrentMeeting(true) // true = end for all (host)
                // endZoomApi fires automatically via listener
            } else {
                // Not in meeting room, call API directly
                viewModel.hitEndZoomApi(
                    "Bearer $accessToken",
                    EndZoomClassRequest(batch_id = batch_id)
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 9. Cleanup
    // ─────────────────────────────────────────────────────────────
    override fun onDestroyView() {
        super.onDestroyView()
        ZoomSDK.getInstance().meetingService?.removeListener(zoomMeetingListener)
    }

    override fun getLayoutId(): Int = R.layout.fragment_teacher_zoom
    override fun restoreView() {}
}