package io.stacrypt.stadroid.ext

import io.stacrypt.stadroid.data.Market
import io.stacrypt.stadroid.data.MarketLast
import java.math.BigDecimal

fun Market.calculateEstimatedFee(
    type: String,
    side: String,
    amount: BigDecimal,
    price: BigDecimal,
    lastPrice: MarketLast
): BigDecimal? = when (type) {
    "limit" -> when (side) {
        "buy" -> when {
            price >= lastPrice.price -> takerCommissionRate?.toBigDecimal()?.times(amount)
            else -> makerCommissionRate?.toBigDecimal()?.times(amount)
        }
        "sell" -> when {
            price <= lastPrice.price -> takerCommissionRate?.toBigDecimal()?.times(amount)
            else -> makerCommissionRate?.toBigDecimal()?.times(amount)
        }
        else -> null
    }
    "market" -> takerCommissionRate?.toBigDecimal()?.times(amount)
    else -> null
}?.scaleByPowerOfTen(-2)?.times(price)

