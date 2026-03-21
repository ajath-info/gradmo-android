package com.app.hihlo.model.search_user_list.response

data class SearchUserListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val pagination: Pagination,
        val searchInfo: SearchInfo,
        val users: List<User>
    ) {
        data class Pagination(
            val currentPage: Int,
            val hasNextPage: Boolean,
            val hasPrevPage: Boolean,
            val limit: Int,
            val resultsReturned: Int,
            val total: Int,
            val totalPages: Int
        )

        data class SearchInfo(
            val searchKey: String,
            val searchedFields: List<String>
        )

        data class User(
            val about: String,
            val city: String,
            val country: String,
            val email: String,
            val gender_name: String,
            val id: Int,
            val interest_name: String,
            val isStoryUploaded: Int,
            val is_seen: Int,
            val is_creator: Boolean,
            val is_verified: Boolean,
            val myStory: MyStory,
            val name: String,
            val profile_image: String,
            val role: String,
            val user_live_status: String,
            val username: String
        ) {
            data class MyStory(
                val asset_type: String,
                val id: Int,
                val seen_count: Int,
                val url: String
            )
        }
    }
}