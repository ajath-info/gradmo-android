package com.app.hihlo.model.post_comments.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostCommentsRequest(var comment:String?=null):Parcelable
