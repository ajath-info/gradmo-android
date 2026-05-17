package com.app.gradmo.ui.activity

// ZoomVideoActivity.kt
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import us.zoom.sdk.IncomingLiveStreamStatus
import us.zoom.sdk.RealTimeMediaStreamsFailReason
import us.zoom.sdk.RealTimeMediaStreamsStatus
import us.zoom.sdk.SubSessionKit
import us.zoom.sdk.SubSessionUserHelpRequestHandler
import us.zoom.sdk.UVCCameraStatus
import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKAnnotationHelper
import us.zoom.sdk.ZoomVideoSDKAnnotationToolType
import us.zoom.sdk.ZoomVideoSDKAudioHelper
import us.zoom.sdk.ZoomVideoSDKAudioOption
import us.zoom.sdk.ZoomVideoSDKAudioRawData
import us.zoom.sdk.ZoomVideoSDKBroadcastControlStatus
import us.zoom.sdk.ZoomVideoSDKCRCCallStatus
import us.zoom.sdk.ZoomVideoSDKCameraControlRequestHandler
import us.zoom.sdk.ZoomVideoSDKCameraControlRequestType
import us.zoom.sdk.ZoomVideoSDKChatHelper
import us.zoom.sdk.ZoomVideoSDKChatMessage
import us.zoom.sdk.ZoomVideoSDKChatMessageDeleteType
import us.zoom.sdk.ZoomVideoSDKChatPrivilegeType
import us.zoom.sdk.ZoomVideoSDKDataType
import us.zoom.sdk.ZoomVideoSDKDelegate
import us.zoom.sdk.ZoomVideoSDKExportFormat
import us.zoom.sdk.ZoomVideoSDKFileTransferStatus
import us.zoom.sdk.ZoomVideoSDKLiveStreamHelper
import us.zoom.sdk.ZoomVideoSDKLiveStreamStatus
import us.zoom.sdk.ZoomVideoSDKLiveTranscriptionHelper
import us.zoom.sdk.ZoomVideoSDKMultiCameraStreamStatus
import us.zoom.sdk.ZoomVideoSDKNetworkStatus
import us.zoom.sdk.ZoomVideoSDKPasswordHandler
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason
import us.zoom.sdk.ZoomVideoSDKPhoneStatus
import us.zoom.sdk.ZoomVideoSDKProxySettingHandler
import us.zoom.sdk.ZoomVideoSDKQOSStatistics
import us.zoom.sdk.ZoomVideoSDKRawDataPipe
import us.zoom.sdk.ZoomVideoSDKReceiveFile
import us.zoom.sdk.ZoomVideoSDKRecordingConsentHandler
import us.zoom.sdk.ZoomVideoSDKRecordingStatus
import us.zoom.sdk.ZoomVideoSDKSSLCertificateInfo
import us.zoom.sdk.ZoomVideoSDKSendFile
import us.zoom.sdk.ZoomVideoSDKSession
import us.zoom.sdk.ZoomVideoSDKSessionContext
import us.zoom.sdk.ZoomVideoSDKSessionLeaveReason
import us.zoom.sdk.ZoomVideoSDKShareAction
import us.zoom.sdk.ZoomVideoSDKShareHelper
import us.zoom.sdk.ZoomVideoSDKShareSetting
import us.zoom.sdk.ZoomVideoSDKShareStatus
import us.zoom.sdk.ZoomVideoSDKStreamingJoinStatus
import us.zoom.sdk.ZoomVideoSDKSubSessionManager
import us.zoom.sdk.ZoomVideoSDKSubSessionParticipant
import us.zoom.sdk.ZoomVideoSDKSubSessionStatus
import us.zoom.sdk.ZoomVideoSDKTestMicStatus
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKUserHelpRequestResult
import us.zoom.sdk.ZoomVideoSDKUserHelper
import us.zoom.sdk.ZoomVideoSDKVideoAspect
import us.zoom.sdk.ZoomVideoSDKVideoCanvas
import us.zoom.sdk.ZoomVideoSDKVideoHelper
import us.zoom.sdk.ZoomVideoSDKVideoOption
import us.zoom.sdk.ZoomVideoSDKVideoSubscribeFailReason
import us.zoom.sdk.ZoomVideoSDKVideoView
import us.zoom.sdk.ZoomVideoSDKWhiteboardHelper

class ZoomVideoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SESSION_NAME = "session_name"
        const val EXTRA_SESSION_TOKEN = "session_token"
        const val EXTRA_USER_NAME = "user_name"
        private const val TAG = "ZoomVideoActivity"
    }

    private lateinit var zoomVideoSDK: ZoomVideoSDK
    private var currentSession: ZoomVideoSDKSession? = null
    private var localVideoView: ZoomVideoSDKVideoView? = null

    private val sessionListener = object : ZoomVideoSDKDelegate {

        override fun onSessionJoin() {
            Log.d(TAG, "Session joined successfully")
            Toast.makeText(this@ZoomVideoActivity, "Joined session!", Toast.LENGTH_SHORT).show()
            renderLocalVideo()
        }

        override fun onSessionLeave() {
            Log.d(TAG, "Session left")
            finish()
        }

        override fun onSessionLeave(reason: ZoomVideoSDKSessionLeaveReason?) {
            TODO("Not yet implemented")
        }

        /*override fun onSessionLeaveReturnReason(reason: ZoomVideoSDKSessionLeaveReason) {
            Log.d(TAG, "Session left reason: $reason")
        }*/

        override fun onError(errorCode: Int) {
            Log.e(TAG, "SDK Error: $errorCode")
            Toast.makeText(this@ZoomVideoActivity, "Error: $errorCode", Toast.LENGTH_LONG).show()
        }

        override fun onUserJoin(userHelper: ZoomVideoSDKUserHelper?, userList: MutableList<ZoomVideoSDKUser>?) {
            Log.d(TAG, "User joined: ${userList?.size}")
        }

        override fun onUserLeave(userHelper: ZoomVideoSDKUserHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
        override fun onUserVideoStatusChanged(videoHelper: ZoomVideoSDKVideoHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
        override fun onShareNetworkStatusChanged(
            shareNetworkStatus: ZoomVideoSDKNetworkStatus?,
            isSendingShare: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun onUserAudioStatusChanged(audioHelper: ZoomVideoSDKAudioHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
        override fun onUserShareStatusChanged(shareHelper: ZoomVideoSDKShareHelper?, userInfo: ZoomVideoSDKUser?, status: ZoomVideoSDKShareStatus?) {}
        override fun onUserShareStatusChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            userInfo: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            TODO("Not yet implemented")
        }

        override fun onShareContentChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            userInfo: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            TODO("Not yet implemented")
        }

        override fun onLiveStreamStatusChanged(liveStreamHelper: ZoomVideoSDKLiveStreamHelper?, status: ZoomVideoSDKLiveStreamStatus?) {}
        override fun onChatNewMessageNotify(chatHelper: ZoomVideoSDKChatHelper?, messageItem: ZoomVideoSDKChatMessage?) {}
        override fun onChatDeleteMessageNotify(
            chatHelper: ZoomVideoSDKChatHelper?,
            msgID: String?,
            deleteBy: ZoomVideoSDKChatMessageDeleteType?
        ) {
            TODO("Not yet implemented")
        }

        override fun onChatPrivilegeChanged(
            chatHelper: ZoomVideoSDKChatHelper?,
            currentPrivilege: ZoomVideoSDKChatPrivilegeType?
        ) {
            TODO("Not yet implemented")
        }

        override fun onUserHostChanged(userHelper: ZoomVideoSDKUserHelper?, userInfo: ZoomVideoSDKUser?) {}
        override fun onUserManagerChanged(user: ZoomVideoSDKUser?) {}
        override fun onUserNameChanged(user: ZoomVideoSDKUser?) {}
        override fun onUserActiveAudioChanged(audioHelper: ZoomVideoSDKAudioHelper?, list: MutableList<ZoomVideoSDKUser>?) {}
        override fun onSessionNeedPassword(handler: ZoomVideoSDKPasswordHandler?) {}
        override fun onSessionPasswordWrong(handler: ZoomVideoSDKPasswordHandler?) {}
        override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {}
        override fun onOneWayAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?, user: ZoomVideoSDKUser?) {}
        override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {}
        override fun onCommandReceived(sender: ZoomVideoSDKUser?, strCmd: String?) {}
        override fun onCommandChannelConnectResult(isSuccess: Boolean) {}
        override fun onCloudRecordingStatus(status: ZoomVideoSDKRecordingStatus?, handler: ZoomVideoSDKRecordingConsentHandler?) {}
        override fun onHostAskUnmute() {}
        override fun onInviteByPhoneStatus(
            status: ZoomVideoSDKPhoneStatus?,
            reason: ZoomVideoSDKPhoneFailedReason?
        ) {
            TODO("Not yet implemented")
        }

        override fun onMultiCameraStreamStatusChanged(
            status: ZoomVideoSDKMultiCameraStreamStatus?,
            user: ZoomVideoSDKUser?,
            videoPipe: ZoomVideoSDKRawDataPipe?
        ) {
            TODO("Not yet implemented")
        }

        override fun onMultiCameraStreamStatusChanged(
            status: ZoomVideoSDKMultiCameraStreamStatus,
            user: ZoomVideoSDKUser,
            canvas: ZoomVideoSDKVideoCanvas
        ) {}

        override fun onLiveTranscriptionStatus(status: ZoomVideoSDKLiveTranscriptionHelper.ZoomVideoSDKLiveTranscriptionStatus?) {
            TODO("Not yet implemented")
        }

        override fun onOriginalLanguageMsgReceived(messageInfo: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionMessageInfo?) {
            TODO("Not yet implemented")
        }

        override fun onLiveTranscriptionMsgInfoReceived(messageInfo: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionMessageInfo?) {
            TODO("Not yet implemented")
        }

        override fun onLiveTranscriptionMsgError(
            spokenLanguage: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage?,
            transcriptLanguage: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage?
        ) {
            TODO("Not yet implemented")
        }

        override fun onSpokenLanguageChanged(spokenLanguage: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage?) {
            TODO("Not yet implemented")
        }

        override fun onProxySettingNotification(handler: ZoomVideoSDKProxySettingHandler?) {
            TODO("Not yet implemented")
        }

        override fun onSSLCertVerifiedFailNotification(info: ZoomVideoSDKSSLCertificateInfo?) {
            TODO("Not yet implemented")
        }

        override fun onCameraControlRequestResult(
            user: ZoomVideoSDKUser?,
            isApproved: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun onCameraControlRequestReceived(
            user: ZoomVideoSDKUser?,
            requestType: ZoomVideoSDKCameraControlRequestType?,
            requestHandler: ZoomVideoSDKCameraControlRequestHandler?
        ) {
            TODO("Not yet implemented")
        }

        override fun onUserVideoNetworkStatusChanged(
            status: ZoomVideoSDKNetworkStatus?,
            user: ZoomVideoSDKUser?
        ) {
            TODO("Not yet implemented")
        }

        override fun onUserRecordingConsent(user: ZoomVideoSDKUser?) {
            TODO("Not yet implemented")
        }

        override fun onCallCRCDeviceStatusChanged(status: ZoomVideoSDKCRCCallStatus?) {
            TODO("Not yet implemented")
        }

        override fun onVideoCanvasSubscribeFail(
            fail_reason: ZoomVideoSDKVideoSubscribeFailReason?,
            pUser: ZoomVideoSDKUser?,
            view: ZoomVideoSDKVideoView?
        ) {
            TODO("Not yet implemented")
        }

        override fun onShareCanvasSubscribeFail(
            fail_reason: ZoomVideoSDKVideoSubscribeFailReason?,
            pUser: ZoomVideoSDKUser?,
            view: ZoomVideoSDKVideoView?
        ) {
            TODO("Not yet implemented")
        }

        override fun onShareCanvasSubscribeFail(
            pUser: ZoomVideoSDKUser?,
            view: ZoomVideoSDKVideoView?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            TODO("Not yet implemented")
        }

        override fun onAnnotationHelperCleanUp(helper: ZoomVideoSDKAnnotationHelper?) {
            TODO("Not yet implemented")
        }

        override fun onAnnotationPrivilegeChange(
            shareOwner: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            TODO("Not yet implemented")
        }

        override fun onAnnotationToolTypeChanged(
            helper: ZoomVideoSDKAnnotationHelper?,
            view: ZoomVideoSDKVideoView?,
            toolType: ZoomVideoSDKAnnotationToolType?
        ) {
            TODO("Not yet implemented")
        }

        override fun onTestMicStatusChanged(status: ZoomVideoSDKTestMicStatus?) {
            TODO("Not yet implemented")
        }

        override fun onMicSpeakerVolumeChanged(micVolume: Int, speakerVolume: Int) {
            TODO("Not yet implemented")
        }

        override fun onCalloutJoinSuccess(
            user: ZoomVideoSDKUser?,
            phoneNumber: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun onSendFileStatus(
            file: ZoomVideoSDKSendFile?,
            status: ZoomVideoSDKFileTransferStatus?
        ) {
            TODO("Not yet implemented")
        }

        override fun onReceiveFileStatus(
            file: ZoomVideoSDKReceiveFile?,
            status: ZoomVideoSDKFileTransferStatus?
        ) {
            TODO("Not yet implemented")
        }

        override fun onUVCCameraStatusChange(
            cameraId: String?,
            status: UVCCameraStatus?
        ) {
            TODO("Not yet implemented")
        }

        override fun onVideoAlphaChannelStatusChanged(isAlphaModeOn: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onSpotlightVideoChanged(
            videoHelper: ZoomVideoSDKVideoHelper?,
            userList: List<ZoomVideoSDKUser?>?
        ) {
            TODO("Not yet implemented")
        }

        override fun onFailedToStartShare(
            shareHelper: ZoomVideoSDKShareHelper?,
            user: ZoomVideoSDKUser?
        ) {
            TODO("Not yet implemented")
        }

        override fun onBindIncomingLiveStreamResponse(
            bSuccess: Boolean,
            streamKeyID: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun onUnbindIncomingLiveStreamResponse(
            bSuccess: Boolean,
            streamKeyID: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun onIncomingLiveStreamStatusResponse(
            bSuccess: Boolean,
            streamsStatusList: List<IncomingLiveStreamStatus?>?
        ) {
            TODO("Not yet implemented")
        }

        override fun onStartIncomingLiveStreamResponse(
            bSuccess: Boolean,
            streamKeyID: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun onStopIncomingLiveStreamResponse(
            bSuccess: Boolean,
            streamKeyID: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun onShareContentSizeChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            user: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            TODO("Not yet implemented")
        }

        override fun onSubSessionStatusChanged(
            status: ZoomVideoSDKSubSessionStatus?,
            subSessionKitList: List<SubSessionKit?>?
        ) {
            TODO("Not yet implemented")
        }

        override fun onSubSessionManagerHandle(manager: ZoomVideoSDKSubSessionManager?) {
            TODO("Not yet implemented")
        }

        override fun onSubSessionParticipantHandle(participant: ZoomVideoSDKSubSessionParticipant?) {
            TODO("Not yet implemented")
        }

        override fun onSubSessionUsersUpdate(subSessionKit: SubSessionKit?) {
            TODO("Not yet implemented")
        }

        override fun onBroadcastMessageFromMainSession(
            message: String?,
            userName: String?
        ) {
            TODO("Not yet implemented")
        }

        override fun onSubSessionUserHelpRequest(handler: SubSessionUserHelpRequestHandler?) {
            TODO("Not yet implemented")
        }

        override fun onSubSessionUserHelpRequestResult(eResult: ZoomVideoSDKUserHelpRequestResult?) {
            TODO("Not yet implemented")
        }

        override fun onShareSettingChanged(setting: ZoomVideoSDKShareSetting?) {
            TODO("Not yet implemented")
        }

        override fun onStartBroadcastResponse(bSuccess: Boolean, channelID: String?) {
            TODO("Not yet implemented")
        }

        override fun onStopBroadcastResponse(bSuccess: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onGetBroadcastControlStatus(
            bSuccess: Boolean,
            status: ZoomVideoSDKBroadcastControlStatus?
        ) {
            TODO("Not yet implemented")
        }

        override fun onStreamingJoinStatusChanged(status: ZoomVideoSDKStreamingJoinStatus?) {
            TODO("Not yet implemented")
        }

        override fun onUserWhiteboardShareStatusChanged(
            user: ZoomVideoSDKUser?,
            helper: ZoomVideoSDKWhiteboardHelper?
        ) {
            TODO("Not yet implemented")
        }

        override fun onWhiteboardExported(
            format: ZoomVideoSDKExportFormat?,
            data: ByteArray?
        ) {
            TODO("Not yet implemented")
        }

        override fun onCanvasSnapshotTaken(
            user: ZoomVideoSDKUser?,
            isShare: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun onCanvasSnapshotIncompatible(user: ZoomVideoSDKUser?) {
            TODO("Not yet implemented")
        }

        override fun onQOSStatisticsReceived(
            statistics: ZoomVideoSDKQOSStatistics?,
            user: ZoomVideoSDKUser?
        ) {
            TODO("Not yet implemented")
        }

        override fun onMyAudioSourceTypeChanged(device: ZoomVideoSDKAudioHelper.ZoomVideoSDKAudioDevice?) {
            TODO("Not yet implemented")
        }

        override fun onUserNetworkStatusChanged(
            type: ZoomVideoSDKDataType?,
            level: ZoomVideoSDKNetworkStatus?,
            user: ZoomVideoSDKUser?
        ) {
            TODO("Not yet implemented")
        }

        override fun onUserOverallNetworkStatusChanged(
            level: ZoomVideoSDKNetworkStatus?,
            user: ZoomVideoSDKUser?
        ) {
            TODO("Not yet implemented")
        }

        override fun onAudioLevelChanged(
            level: Int,
            audioSharing: Boolean,
            user: ZoomVideoSDKUser?
        ) {
            TODO("Not yet implemented")
        }

        override fun onRealTimeMediaStreamsStatus(status: RealTimeMediaStreamsStatus?) {
            TODO("Not yet implemented")
        }

        override fun onRealTimeMediaStreamsFail(failReason: RealTimeMediaStreamsFailReason?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionName = intent.getStringExtra(EXTRA_SESSION_NAME) ?: "test-session"
        val sessionToken = intent.getStringExtra(EXTRA_SESSION_TOKEN) ?: ""
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "Student"

        // Simple layout — one full-screen video view
        localVideoView = ZoomVideoSDKVideoView(this)
        setContentView(localVideoView)

        zoomVideoSDK = ZoomVideoSDK.getInstance()
        zoomVideoSDK.addListener(sessionListener)

        joinSession(sessionName, sessionToken, userName)
    }

    private fun joinSession(sessionName: String, token: String, userName: String) {
        val audioOption = ZoomVideoSDKAudioOption().apply {
            connect = true
            mute = false
        }
        val videoOption = ZoomVideoSDKVideoOption().apply {
            localVideoOn = true
        }
        val sessionContext = ZoomVideoSDKSessionContext().apply {
            this.sessionName = sessionName
            this.token = token
            this.userName = userName
            this.audioOption = audioOption
            this.videoOption = videoOption
        }

        val result = zoomVideoSDK.joinSession(sessionContext)
        Log.d(TAG, "joinSession result: $result")
    }

    private fun renderLocalVideo() {
        val myUser = zoomVideoSDK.session?.mySelf ?: return
        myUser.videoCanvas?.subscribe(localVideoView, ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_LetterBox)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentSession?.let {
            zoomVideoSDK.leaveSession(false)
        }
        zoomVideoSDK.removeListener(sessionListener)
    }
}