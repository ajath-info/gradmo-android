package com.app.gradmo.model.plan_detail

data class PlanDetailsResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val pagination: Pagination,
        val plans: List<Plan>
    ) {
        data class Pagination(
            val limit: Int,
            val page: Int,
            val total: Int,
            val totalPages: Int,
            val totalRecords: Int
        )

        data class Plan(
            val amount: Int,
            val createdAt: String,
            val description: String,
            val planId: Int,
            val planName: String,
            val planType: String,
            val status: Int,
            val validityDays: Int
        )
    }
}