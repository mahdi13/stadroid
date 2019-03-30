package io.stacrypt.stadroid.ui

import java.math.BigDecimal
import java.math.RoundingMode


enum class Threshold private constructor(
    aValueString: String, val numberOfZeroes: Int, protected var suffix: Char?,
    val higherThreshold: Threshold?
) {
    TRILLION("1000000000000", 12, 't', null),
    BILLION("1000000000", 9, 'b', TRILLION),
    MILLION("1000000", 6, 'm', BILLION),
    THOUSAND("1000", 3, 'k', MILLION),
    ZERO("0", 0, null, THOUSAND);

    private val value: BigDecimal

    init {
        value = BigDecimal(aValueString)
    }

    fun getSuffix(): String {
        return if (suffix == null) "" else "" + suffix!!
    }

    companion object {

        fun thresholdFor(aValue: Long): Threshold {
            return thresholdFor(BigDecimal(aValue))
        }

        fun thresholdFor(aValue: BigDecimal): Threshold {
            for (eachThreshold in Threshold.values()) {
                if (eachThreshold.value.compareTo(aValue) <= 0) {
                    return eachThreshold
                }
            }
            return TRILLION // shouldn't be needed, but you might have to extend the enum
        }
    }
}


object NumberShortener {

    val REQUIRED_PRECISION = 2

    fun toPrecisionWithoutLoss(
        aBigDecimal: BigDecimal,
        aPrecision: Int, aMode: RoundingMode
    ): BigDecimal {
        val previousScale = aBigDecimal.scale()
        val previousPrecision = aBigDecimal.precision()
        val newPrecision = Math.max(previousPrecision - previousScale, aPrecision)
        return aBigDecimal.setScale(
            previousScale + newPrecision - previousPrecision,
            aMode
        )
    }

    private fun scaledNumber(aNumber: BigDecimal, aMode: RoundingMode): BigDecimal {
        val threshold = Threshold.thresholdFor(aNumber)
        val adjustedNumber = aNumber.movePointLeft(threshold.numberOfZeroes)
// System.out.println("Number: <" + aNumber + ">, adjusted: <" + adjustedNumber
        // + ">, rounded: <" + scaledNumber + ">");
        return toPrecisionWithoutLoss(
            adjustedNumber, REQUIRED_PRECISION,
            aMode
        ).stripTrailingZeros()
    }

    fun shortenedNumber(aNumber: Long, aMode: RoundingMode): String {
        val isNegative = aNumber < 0
        val numberAsBigDecimal = BigDecimal(if (isNegative) -aNumber else aNumber)
        var threshold: Threshold? = Threshold.thresholdFor(numberAsBigDecimal)
        var scaledNumber = if (aNumber == 0L)
            numberAsBigDecimal
        else
            scaledNumber(
                numberAsBigDecimal, aMode
            )
        if (scaledNumber.compareTo(BigDecimal("1000")) >= 0) {
            scaledNumber = scaledNumber(scaledNumber, aMode)
            threshold = threshold?.higherThreshold
        }
        val sign = if (isNegative) "-" else ""
// System.out.println("Number: <" + sign + numberAsBigDecimal + ">, rounded: <"
        // + sign + scaledNumber + ">, print: <" + printNumber + ">");
        return (sign + scaledNumber.stripTrailingZeros().toPlainString()
                + threshold?.getSuffix())
    }
}
