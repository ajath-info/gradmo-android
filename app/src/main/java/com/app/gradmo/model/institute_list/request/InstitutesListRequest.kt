package com.app.gradmo.model.institute_list.request

data class InstitutesListRequest(
    val latitude:String,
    val longitude:String,
    val order_field:String,
    val order_type:String,
    val search:String?=null,
    val page:String?=null,
    val limit:String?=null,
    val mode:String?=null,
    val city:String?=null,
    )
