package com.app.gradmo.model.exam_list

data class ExamsListRequest(
    val batch_id: String?=null,
    val page: String?=null,
    val limit: String?=null,
)
