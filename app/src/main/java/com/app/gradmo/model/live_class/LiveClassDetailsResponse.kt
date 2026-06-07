package com.app.gradmo.model.live_class

data class LiveClassDetailsResponse(
    val liveClass: LiveClass,
    val message: String,
    val msg: String,
    val status: String
) {
    data class LiveClass(
        val adminId: Int,
        val batchId: Int,
        val chapterId: Int,
        val chapterName: String,
        val date: String,
        val endTime: String,
        val entryDateTime: String,
        val isBatchZoom: Int,
        val isLive: Int,
        val liveClassId: Int,
        val meeting: Meeting,
        val startTime: String,
        val subjectId: Int,
        val subjectName: String,
        val teacherId: Int,
        val teacherImage: String,
        val teacherImageUrl: String,
        val teacherName: String,
        val typeClass: Int,
        val typeLabel: String
    ) {
        data class Meeting(
            val canJoin: Int,
            val classStarted: Int,
            val displayName: String,
            val isHost: Int,
            val joinReady: Int,
            val meetingNumber: String,
            val password: String,
            val role: Int,
            val sdkConfigHint: String,
            val sdkKey: String,
            val signature: String,
            val signatureAlt: String,
            val signatureAltMode: String,
            val signatureMode: String,
            val timeMessage: String,
            val type: String,
            val zak: String
        )
    }
}