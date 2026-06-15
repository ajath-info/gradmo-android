package com.app.gradmo.model.notification_list

data class NotificationListResponse(
    val msg: String,
    val notifications: List<Notification>,
    val pagination: Pagination,
    val status: String,
    val userType: String
) {
    data class Notification(
        val batchId: String,
        val id: String,
        val msg: String,
        val notificationType: String,
        val seenBy: String,
        val status: String,
        val studentId: String,
        val time: String,
        val url: String
    )

    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )
}