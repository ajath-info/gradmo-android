package com.app.gradmo.utils.network_utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.app.gradmo.R

object ProcessDialog {
    private var progressDialog: Dialog? = null

    fun showDialog(context: Context?, isDialog: Boolean) {
        if (isDialog) {
            if (context != null) {
                start(context)
            }
        }
    }

    fun dismissDialog(isDialog: Boolean) {
        if (isDialog) {
            dismiss()
        }
    }
    fun start(context: Context) {
        if (!isShowing()) {
            val activity = context.getActivity() ?: return
            if (!activity.isFinishing && !activity.isDestroyed) {
                progressDialog = Dialog(activity)  // use activity, not context
                progressDialog?.setCancelable(false)
                progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                progressDialog?.setContentView(R.layout.view_progress_dialog)
                progressDialog?.show()
            }
        }
    }

    // Extension to safely unwrap Activity from any Context
    fun Context.getActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }


    fun dismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: IllegalArgumentException) {
            // Handle or log or ignore
        } catch (e: Exception) {
            // Handle or log or ignore
        } finally {
            progressDialog = null
        }
    }


    fun isShowing(): Boolean {
        return if (progressDialog != null) {
            progressDialog!!.isShowing
        } else {
            false
        }
    }
}
