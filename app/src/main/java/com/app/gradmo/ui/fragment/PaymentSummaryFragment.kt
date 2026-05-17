package com.app.gradmo.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentPaymentSummaryBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.promocode.response.PromocodeListResponse
import com.app.gradmo.model.verify_payment.VerifyPaymentRequest
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.PAYMENT_GATEWAY_ID
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.RazorpayPaymentActivity
import com.app.gradmo.ui.view_model.PaymentSummaryViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentSummaryFragment : BaseFragment<FragmentPaymentSummaryBinding>() {  // ← Razorpay result listener
    private val viewModel: PaymentSummaryViewModel by viewModels()
    // ─── Required Parameters ───────────────────────────────────────────
    private var RAZORPAY_KEY_ID = ""
    private var userEmail: String = ""     // Prefill user email
    private var userContact: String = ""   // Prefill user phone
    private var userName: String = ""      // Prefill user name
    private var batch_price = ""
    private var batch_offer_price = ""
    private var batch_id = ""
    private var platform_fee = ""
    private var grandTotal = ""
    private var subTotal = ""
    private lateinit var promoCode: PromocodeListResponse.Data.PromoCode

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            batch_price = it.getString("batch_price").toString()
            batch_offer_price = it.getString("batch_offer_price").toString()
            batch_id = it.getString("batch_id").toString()
            platform_fee = it.getString("platform_fee").toString()
        }
        Log.i("TAG", "payment summary: "+batch_price)
        Log.i("TAG", "payment summary: "+batch_offer_price)
        Log.i("TAG", "payment summary: "+batch_id)
        Log.i("TAG", "payment summary: "+platform_fee)
        // ── Pre-warm Razorpay (optional but recommended for faster checkout) ──
        Checkout.preload(requireContext())
        RAZORPAY_KEY_ID = Preferences.getStringPreference(requireContext(), PAYMENT_GATEWAY_ID) ?: ""
        Log.i("TAG", "RAZORPAY_KEY_ID: "+RAZORPAY_KEY_ID)
        setupUI()
    }

    private fun setupUI() {
            binding.tutionFee.text = "₹ ${batch_price}/month"
            binding.enrollmentFee.text = "₹ ${batch_offer_price}"
            this.subTotal = (batch_offer_price.toInt()*12).toString()
            binding.subTotal.text = "₹ ${subTotal}"
            binding.tvTotal.text = "₹ ${batch_offer_price.toInt()*12}"
            binding.grandSubTotal.text = "₹ ${subTotal}"
            binding.platformFee.text = "₹ ${platform_fee}"
            grandTotal = "${platform_fee.toInt() + (batch_offer_price.toInt()*12)}"
            binding.tvGrandTotal.text = "₹ ${grandTotal}"
            binding.tvFinalTotal.text = "₹ ${grandTotal}"
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
            ?.getLiveData<PromocodeListResponse.Data.PromoCode>("selected_promo_code")
            ?.observe(viewLifecycleOwner) { promoCode ->
                promoCode?.let {
                    Log.d("TAG", "Received promo code: ${Gson().toJson(promoCode)}")
                    setupUI()
                    this.promoCode = promoCode
                    binding.promoCodeText.setText(promoCode.code)

                    binding.tvFinalTotal.text = "₹ ${platform_fee.toInt() + (batch_offer_price.toInt()*12 - promoCode.discountValue)}"
                    grandTotal = "${platform_fee.toInt() + (batch_offer_price.toInt()*12 - promoCode.discountValue)}"
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
                    if (it.data?.status == "true") {
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

    override fun restoreView() {
        setupUI()
    }

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
            val loginData = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data
            val accessToken = loginData?.accessToken
            val studentId = loginData?.studentId
            var request = VerifyPaymentRequest(
                batch_offer_price = batch_offer_price,
                grand_total_before_discount = if(::promoCode.isInitialized)(grandTotal.toInt() - promoCode.discountValue.toInt()).toString() else null,
                batch_price = batch_price,
                tuition_fee = (batch_price.toInt()*12).toString(),
                discount_amount = if (::promoCode.isInitialized) promoCode.discountValue.toString() else "0",
                currency = "INR",
                tuition_12_month_total = (batch_price.toInt() *12).toString(),
                renewal_plan_id = 0,
                razorpay_order_id = orderId.toString(),
                monthly_subtotal = batch_price,
                total_payable = grandTotal,
                batch_id = batch_id,
                razorpay_signature = signature.toString(),
                razorpay_payment_id = paymentId.toString(),
                student_id = studentId.toString(),
                first_payment_plan_id = 0
            )
            viewModel.hitVerifyPaymentDataApi("Bearer $accessToken", request)

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
            putExtra(RazorpayPaymentActivity.EXTRA_AMOUNT,       grandTotal.toInt()*100)
            putExtra(RazorpayPaymentActivity.EXTRA_USER_NAME,    userName)
            putExtra(RazorpayPaymentActivity.EXTRA_USER_EMAIL,   userEmail)
            putExtra(RazorpayPaymentActivity.EXTRA_USER_CONTACT, userContact)
        }
        paymentLauncher.launch(intent)
    }
}