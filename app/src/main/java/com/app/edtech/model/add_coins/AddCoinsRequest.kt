package com.app.hihlo.model.add_coins

data class AddCoinsRequest(val coins: String? = null,
                           val payment_id: String? = null,
                           val amount: String? = null,
    )
