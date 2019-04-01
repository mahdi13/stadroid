package io.stacrypt.stadroid.ui

import io.stacrypt.stadroid.data.Currency
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun BigDecimal.format10Digit() = when {
    this < 10.toBigDecimal() -> this.format(8)
    this < 1_000_000_000.toBigDecimal() -> java.lang.String.format(Locale.ENGLISH, "%.8f", this)
        .substring(0, 9)
        .toBigDecimal()
        .format()
        .run { if (substringBeforeLast(".").length > 9) substringBeforeLast(".") else this }
//    this < 1_000_000_000.toBigDecimal() -> java.lang.String.format("%.8f", this.scaleByPowerOfTen(-6))
//        .substring(0, 9)
//        .toBigDecimal()
//        .format()
//        .plus(" M")
    this < 1_000_000_000_000.toBigDecimal() -> java.lang.String.format(
        Locale.ENGLISH,
        "%.8f",
        this.scaleByPowerOfTen(-9)
    )
        .substring(0, 9)
        .toBigDecimal()
        .format()
        .plus(" B")
    this < BigDecimal("1000000000000000") -> java.lang.String.format(
        Locale.ENGLISH,
        "%.8f",
        this.scaleByPowerOfTen(-12)
    )
        .substring(0, 9)
        .toBigDecimal()
        .format()
        .plus(" T")
    else -> java.lang.String.format(Locale.ENGLISH, "%,.0f", this)
}

fun BigDecimal.formatShort() =
    NumberShortener.toPrecisionWithoutLoss(this, 8, RoundingMode.HALF_EVEN)
//        .stripTrailingZeros()
        .toPlainString()

fun BigDecimal.format() = java.lang.String.format(Locale.ENGLISH, "%,f", this)

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


fun BigDecimal.formatChangePercentFrom(base: BigDecimal) = if (base == 0.toBigDecimal())
    "0.00 %"
else
    this.abs().divide(base, 8, RoundingMode.HALF_EVEN).scaleByPowerOfTen(2).formatPercent()

fun BigDecimal.formatPercent(digits: Int = 2) = "${this.signSymbol()} ${this.format(digits)} %"
fun BigDecimal.signSymbol() = if (isPositive()) "+" else "-"
fun BigDecimal.isPositive(): Boolean = this >= 0.toBigDecimal()


