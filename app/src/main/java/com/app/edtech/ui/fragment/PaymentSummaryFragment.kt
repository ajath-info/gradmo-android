package com.app.edtech.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.edtech.databinding.FragmentPaymentSummaryBinding
import com.app.edtech.databinding.FragmentSearchInstituteBinding
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.adapter.HomeBannerAdapter
import com.app.edtech.ui.adapter.SearchInstituteAdapter
import com.app.edtech.ui.view_model.SearchInstituteViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.CommonUtils.updateModeUI
import com.app.edtech.utils.CommonUtils.updateSortUI
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PaymentSummaryFragment : BaseFragment<FragmentPaymentSummaryBinding>(),
    PaymentResultWithDataListener {  // ← Razorpay result listener

    // ─── Required Parameters ───────────────────────────────────────────
    private val RAZORPAY_KEY_ID = "rzp_test_XXXXXXXXXXXXXXXX"  // From Razorpay Dashboard
    private var orderId: String = ""       // Generated from YOUR backend (mandatory)
    private var paymentAmount: Int = 0     // Amount in PAISE (e.g., ₹500 = 50000)
    private var userEmail: String = ""     // Prefill user email
    private var userContact: String = ""   // Prefill user phone
    private var userName: String = ""      // Prefill user name

    override fun initView(savedInstanceState: Bundle?) {
        // ── Pre-warm Razorpay (optional but recommended for faster checkout) ──
        Checkout.preload(requireContext())
        setupUI()
    }

    private fun setupUI() {
        // Populate your UI with order/payment details here
        // e.g., binding.amountText.text = "₹${paymentAmount / 100}"
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.paymentButton.setOnClickListener {
            startRazorpayCheckout()
        }
    }

    // ─── Initialize & Launch Razorpay Checkout ─────────────────────────
    private fun startRazorpayCheckout() {
        val checkout = Checkout()
        checkout.setKeyID(RAZORPAY_KEY_ID)

        // Optional: Set your app logo on the Razorpay sheet
        checkout.setImage(R.drawable.ic_launcher_foreground)

        try {
            val options = JSONObject().apply {
                put("name", R.string.app_name)   // Shown on checkout sheet
                put("description", "Payment")       // Short description
                put("order_id", orderId)                  // ← From your backend (MANDATORY)
                put("currency", "INR")
                put("amount", paymentAmount)               // In PAISE

                // Prefill user details (optional but improves UX)
                put("prefill", JSONObject().apply {
                    put("email", userEmail)
                    put("contact", userContact)
                    put("name", userName)
                })

                // Restrict/allow payment methods (optional)
                put("method", JSONObject().apply {
                    put("netbanking", true)
                    put("card", true)
                    put("upi", true)
                    put("wallet", true)
                })

                // Theme color (optional)
                put("theme", JSONObject().apply {
                    put("color", "#6C63FF")  // Your brand color
                })
            }

            // requireActivity() is needed — Razorpay needs an Activity reference
            checkout.open(requireActivity(), options)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error starting payment: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ─── Payment SUCCESS Callback ───────────────────────────────────────
    override fun onPaymentSuccess(razorpayPaymentId: String?, data: PaymentData?) {
        /*
         * razorpayPaymentId → e.g., "pay_XXXXXXXXXXXXXXXXXX"  (verify this on your backend)
         * data.orderId      → matches the orderId you passed
         * data.signature    → use this + orderId + paymentId to verify on backend (HMAC SHA256)
         *
         * ⚠️ IMPORTANT: Always verify the signature on your SERVER before marking order as paid.
         */
        Log.d("Razorpay", "Payment Success: $razorpayPaymentId")
        Log.d("Razorpay", "Order ID: ${data?.orderId}")
        Log.d("Razorpay", "Signature: ${data?.signature}")

        // TODO: Send razorpayPaymentId, orderId, signature to your backend for verification
        // After backend confirms → navigate to success screen
        findNavController().navigate(R.id.successPaymentFragment)
    }

    // ─── Payment FAILURE Callback ───────────────────────────────────────
    override fun onPaymentError(errorCode: Int, errorDescription: String?, data: PaymentData?) {
        /*
         * Common Error Codes:
         *   Checkout.NETWORK_ERROR  (2) → No internet
         *   Checkout.INVALID_OPTIONS(3) → Bad JSON options
         *   Checkout.PAYMENT_CANCELED(0)→ User dismissed the sheet
         *   Checkout.TLS_ERROR      (6) → TLS handshake failed
         */
        Log.e("Razorpay", "Payment Failed: Code=$errorCode | $errorDescription")

        val userMessage = when (errorCode) {
            Checkout.NETWORK_ERROR    -> "No internet connection. Please try again."
            Checkout.PAYMENT_CANCELED -> "Payment was cancelled."
            else                      -> "Payment failed: $errorDescription"
        }

        Toast.makeText(requireContext(), userMessage, Toast.LENGTH_LONG).show()
        // Optionally navigate to a failure screen or stay on this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    override fun getLayoutId(): Int = R.layout.fragment_payment_summary

    private fun setObserver() {}

    override fun restoreView() {}
}