package com.app.hihlo.model.get_reel_comments.response

data class ReelCommentsResponse(
    var error:Boolean,
    var status:Int,
    var code:Int,
    var message:String,
    var payload: Payload,
)
