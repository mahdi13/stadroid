package io.stacrypt.stadroid.ui

import io.stacrypt.stadroid.data.Currency
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min

fun BigDecimal.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun BigDecimal.format(currency: Currency) = format(max(min(currency.smallestUnitScale, 8), 0))

/**
 * This method let us show the large amount of money in a few characters
 */
fun BigDecimal.formatScaledMinimal(currency: Currency, unitIncluded: Boolean = false): String {
    var output: String? = null
    when {
        /**
         * BTC
         */
        currency.symbol.endsWith("IRR") -> {
            when {
                this < "0.000100".toBigDecimal() -> return this
                    .scaleByPowerOfTen(-3)
                    .format(2)
                    .plus(" sat")
                    .plus("sat")
            }
        }

        /**
         * IRR
         */
        currency.symbol.endsWith("IRR") -> {

        }
    }
    return output ?: ""
}
