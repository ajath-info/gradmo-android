package com.app.gradmo.model.verify_payment

data class VerifyPaymentRequest(
    val batch_offer_price: String,
    val grand_total_before_discount: String?=null,
    val batch_price: String,
    val tuition_fee: String,
    val discount_amount: String,
    val currency: String,
    val tuition_12_month_total: String,
    val renewal_plan_id: Int,
    val razorpay_order_id: String,
    val monthly_subtotal: String,
    val total_payable: String,
    val batch_id: String,
    val razorpay_signature: String,
    val razorpay_payment_id: String,
    val student_id: String,
    val first_payment_plan_id: Int
)