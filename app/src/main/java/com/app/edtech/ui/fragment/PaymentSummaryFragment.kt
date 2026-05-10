package com.app.edtech.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentPaymentSummaryBinding
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.verify_payment.VerifyPaymentRequest
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.PAYMENT_GATEWAY_ID
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.activity.RazorpayPaymentActivity
import com.app.edtech.ui.view_model.PaymentSummaryViewModel
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PaymentSummaryFragment : BaseFragment<FragmentPaymentSummaryBinding>() {  // ← Razorpay result listener
    private val viewModel: PaymentSummaryViewModel by viewModels()
    // ─── Required Parameters ───────────────────────────────────────────
    private var RAZORPAY_KEY_ID = ""
    private var paymentAmount: Int = 10000     // Amount in PAISE (e.g., ₹500 = 50000)
    private var userEmail: String = ""     // Prefill user email
    private var userContact: String = ""   // Prefill user phone
    private var userName: String = ""      // Prefill user name

    override fun initView(savedInstanceState: Bundle?) {
        // ── Pre-warm Razorpay (optional but recommended for faster checkout) ──
        Checkout.preload(requireContext())
        RAZORPAY_KEY_ID = Preferences.getStringPreference(requireContext(), PAYMENT_GATEWAY_ID) ?: ""
        Log.i("TAG", "RAZORPAY_KEY_ID: "+RAZORPAY_KEY_ID)
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
            val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
            viewModel.hitCreateOrderDataApi("Bearer $accessToken", "100")
        }
        binding.promoCodeLayout.setOnClickListener {
            findNavController().navigate(R.id.promocodeListFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    override fun getLayoutId(): Int = R.layout.fragment_payment_summary

    private fun setObserver() {
        // In your source fragment's onViewCreated
        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("payment_status")
            ?.observe(viewLifecycleOwner) { status ->
                when (status) {
                    "success" -> { /* update UI, show badge, etc. */ }
                    "failed"  -> { /* show error state */ }
                }
            }

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("selected_promo_code")
            ?.observe(viewLifecycleOwner) { promoCode ->
                if (!promoCode.isNullOrEmpty()) {
                    // Use the promo code here
                    Log.d("TAG", "Received promo code: $promoCode")
                    binding.promoCodeText.setText(promoCode)   // show on UI
//                    applyPromoCode(promoCode)                // call your discount logic
                }
            }
        viewModel.getCreateOrderLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getCreateOrderLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        Log.i("TAG", "getCreateOrderLiveData: "+ Gson().toJson(it.data))
                        startRazorpayCheckout(it.data.order.id)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
        viewModel.getVerifyPaymentLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getVerifyPaymentLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == true) {
                        Log.i("TAG", "getVerifyPaymentLiveData: "+ Gson().toJson(it.data))
                        findNavController().navigate(R.id.successPaymentFragment)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "api Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    override fun restoreView() {}

    // ─── Register launcher (at class level, before onViewCreated) ──────
    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // ── SUCCESS ──────────────────────────────────────────────
            val paymentId = result.data?.getStringExtra(RazorpayPaymentActivity.RESULT_PAYMENT_ID)
            val orderId   = result.data?.getStringExtra(RazorpayPaymentActivity.RESULT_ORDER_ID)
            val signature = result.data?.getStringExtra(RazorpayPaymentActivity.RESULT_SIGNATURE)

            Log.d("Razorpay", "Success — paymentId=$paymentId, orderId=$orderId")
            // TODO: verify signature on your backend before navigating

            // Pass result back to the previous fragment (if needed)
            findNavController()
                .previousBackStackEntry
                ?.savedStateHandle
                ?.set("payment_status", "success")
            val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
//            var request = VerifyPaymentRequest(
//                batch_offer_price = "0",
//                grand_total_before_discount = "0",
//                batch_price = "0",
//                tuition_fee = "0",
//                discount_amount = "0",
//                currency = "INR",
//                tuition_12_month_total = "0",
//                renewal_plan_id = TODO(),
//                razorpay_order_id = orderId.toString(),
//                monthly_subtotal = TODO(),
//                total_payable = TODO(),
//                batch_id = TODO(),
//                razorpay_signature = signature.toString(),
//                razorpay_payment_id = paymentId.toString(),
//                student_id = ,
//                first_payment_plan_id = TODO()
//            )
//            viewModel.hitVerifyPaymentDataApi("Bearer $accessToken", request)

        } else {
            // ── FAILURE / CANCELLED ───────────────────────────────────
            val errorCode = result.data?.getIntExtra(RazorpayPaymentActivity.RESULT_ERROR_CODE, -1)
            val errorDesc = result.data?.getStringExtra(RazorpayPaymentActivity.RESULT_ERROR_DESC)

            val userMessage = when (errorCode) {
                Checkout.NETWORK_ERROR    -> "No internet connection. Please try again."
                Checkout.PAYMENT_CANCELED -> "Payment was cancelled."
                else                      -> "Payment failed: $errorDesc"
            }
            Toast.makeText(requireContext(), userMessage, Toast.LENGTH_LONG).show()

            // Optionally pass failure back too
            findNavController()
                .previousBackStackEntry
                ?.savedStateHandle
                ?.set("payment_status", "failed")
        }
    }

    // ─── Replace startRazorpayCheckout() with this ─────────────────────
    private fun startRazorpayCheckout(orderId: String) {
        val intent = Intent(requireContext(), RazorpayPaymentActivity::class.java).apply {
            putExtra(RazorpayPaymentActivity.EXTRA_KEY_ID,       RAZORPAY_KEY_ID)
            putExtra(RazorpayPaymentActivity.EXTRA_ORDER_ID,     orderId)
            putExtra(RazorpayPaymentActivity.EXTRA_AMOUNT,       paymentAmount)
            putExtra(RazorpayPaymentActivity.EXTRA_USER_NAME,    userName)
            putExtra(RazorpayPaymentActivity.EXTRA_USER_EMAIL,   userEmail)
            putExtra(RazorpayPaymentActivity.EXTRA_USER_CONTACT, userContact)
        }
        paymentLauncher.launch(intent)
    }
}