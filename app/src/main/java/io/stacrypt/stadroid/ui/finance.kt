package io.stacrypt.stadroid.ui

import io.stacrypt.stadroid.data.Currency
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

fun BigDecimal.format10Digit() = when {
    this < 10.toBigDecimal() -> this.format(8)
    this < 100_000_000.toBigDecimal() -> java.lang.String.format("%.f", this).substring(0, 9).toBigDecimal().format()
    else -> java.lang.String.format("%,.0f", this)
}

fun BigDecimal.formatShort() =
    NumberShortener.toPrecisionWithoutLoss(this, 8, RoundingMode.HALF_EVEN)
//        .stripTrailingZeros()
        .toPlainString()

fun BigDecimal.format() = java.lang.String.format("%,f", this)

fun BigDecimal.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)

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
