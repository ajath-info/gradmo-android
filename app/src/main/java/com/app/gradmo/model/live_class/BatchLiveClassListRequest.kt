package com.app.gradmo.model.live_class

data class BatchLiveClassListRequest(
    val batch_id: String?=null,
    val sort_by: String?=null,
    val sort_dir: String?=null,
    val page: String?=null,
    val limit: String?=null,
)