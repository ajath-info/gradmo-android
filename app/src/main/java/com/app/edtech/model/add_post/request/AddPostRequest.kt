package com.app.hihlo.model.add_post.request

data class AddPostRequest(var assetUrl: String, var assetType: String, var caption: String, var postHeightSize: Int?=null)
