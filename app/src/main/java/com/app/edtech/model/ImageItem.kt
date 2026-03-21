package com.app.hihlo.ui.profile.model

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ImageItem(
    var imageUri: Uri? = null
): Parcelable
