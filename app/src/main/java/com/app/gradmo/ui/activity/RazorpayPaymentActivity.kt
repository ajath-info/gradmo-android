package com.app.gradmo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.gradmo.R
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class RazorpayPaymentActivity : AppCompatActivity(), PaymentResultWithDataListener {

    companion object {
        const val EXTRA_ORDER_ID      = "order_id"
        const val EXTRA_AMOUNT        = "amount"
        const val EXTRA_KEY_ID        = "key_id"
        const val EXTRA_USER_NAME     = "user_name"
        const val EXTRA_USER_EMAIL    = "user_email"
        const val EXTRA_USER_CONTACT  = "user_contact"

        const val RESULT_PAYMENT_ID   = "payment_id"
        const val RESULT_ORDER_ID     = "result_order_id"
        const val RESULT_SIGNATURE    = "signature"
        const val RESULT_ERROR_CODE   = "error_code"
        const val RESULT_ERROR_DESC   = "error_desc"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Transparent — user only sees the Razorpay bottom sheet
        setContentView(View(this))
        startRazorpayCheckout()
    }

    private fun startRazorpayCheckout() {
        val orderId     = intent.getStringExtra(EXTRA_ORDER_ID) ?: return finish()
        val amount      = intent.getIntExtra(EXTRA_AMOUNT, 0)
        val keyId       = intent.getStringExtra(EXTRA_KEY_ID) ?: return finish()
        val userName    = intent.getStringExtra(EXTRA_USER_NAME) ?: ""
        val userEmail   = intent.getStringExtra(EXTRA_USER_EMAIL) ?: ""
        val userContact = intent.getStringExtra(EXTRA_USER_CONTACT) ?: ""

        val checkout = Checkout()
        checkout.setKeyID(keyId)
        checkout.setImage(R.drawable.ic_launcher_foreground)

        try {
            val options = JSONObject().apply {
                put("name", getString(R.string.app_name))
                put("description", "Payment")
                put("order_id", orderId)
                put("currency", "INR")
                put("amount", amount)
                put("prefill", JSONObject().apply {
                    put("name", userName)
                    put("email", userEmail)
                    put("contact", userContact)
                })
                put("method", JSONObject().apply {
                    put("netbanking", true)
                    put("card", true)
                    put("upi", true)
                    put("wallet", true)
                })
                put("theme", JSONObject().apply {
                    put("color", "#6C63FF")
                })
            }
            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    // ─── SUCCESS ────────────────────────────────────────────────────────
    override fun onPaymentSuccess(paymentId: String?, data: PaymentData?) {
        val result = Intent().apply {
            putExtra(RESULT_PAYMENT_ID,  paymentId)
            putExtra(RESULT_ORDER_ID,    data?.orderId)
            putExtra(RESULT_SIGNATURE,   data?.signature)
        }
        setResult(RESULT_OK, result)
        finish()
    }

    // ─── FAILURE ────────────────────────────────────────────────────────
    override fun onPaymentError(errorCode: Int, errorDesc: String?, data: PaymentData?) {
        val result = Intent().apply {
            putExtra(RESULT_ERROR_CODE, errorCode)
            putExtra(RESULT_ERROR_DESC, errorDesc)
        }
        setResult(RESULT_CANCELED, result)
        finish()
    }
}