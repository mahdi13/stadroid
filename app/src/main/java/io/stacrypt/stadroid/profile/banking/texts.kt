package io.stacrypt.stadroid.profile.banking

import android.text.TextWatcher
import android.widget.EditText
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import io.stacrypt.stadroid.data.Currency
import io.stacrypt.stadroid.ui.format
import org.iban4j.Iban
import java.lang.Exception
import java.math.BigDecimal
import java.util.regex.Pattern

private val IBAN_REGEX = "^[A-Z]{2}[0-9]{8,30}\$".toRegex()

fun EditText.addBankIbanTextFormatter() = addTextChangedListener(object : TextWatcher {

    private var prev: String = ""

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    private fun String.uglify() = replace(" ", "")
    private fun String.beatifulize() = replace(".{4}".toRegex(), "$0 ")
        .replaceFirst(".{2}".toRegex(), "$0 ")
        .trim()

    private fun String.isPartialIban() = IBAN_REGEX.containsMatchIn(this)

    override fun afterTextChanged(s: Editable) {
        removeTextChangedListener(this)

        val current = s.toString().uglify()

        if (current.uglify().isPartialIban()) s.replace(0, s.length, current.beatifulize())
        else s.replace(0, s.length, prev)

        prev = s.toString()

        addTextChangedListener(this)
    }
})

fun EditText.addBankCardTextFormatter() = addTextChangedListener(object : TextWatcher {
    private val TOTAL_SYMBOLS = 19 // size of pattern 0000-0000-0000-0000
    private val TOTAL_DIGITS = 16 // max numbers of digits in pattern: 0000 x 4
    private val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1
    private val DIVIDER_POSITION = DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0
    private val DIVIDER = '-'

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // noop
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // noop
    }

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
            s.replace(0, s.length, buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER))
        }
    }

    private fun isInputCorrect(s: Editable, totalSymbols: Int, dividerModulo: Int, divider: Char): Boolean {
        var isCorrect = s.length <= totalSymbols // check size of entered string
        for (i in 0 until s.length) { // check that every element is right
            if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect = isCorrect and (divider == s[i])
            } else {
                isCorrect = isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
    }

    private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
        val formatted = StringBuilder()

        for (i in digits.indices) {
            if (digits[i].toInt() != 0) {
                formatted.append(digits[i])
                if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                    formatted.append(divider)
                }
            }
        }

        return formatted.toString()
    }

    private fun getDigitArray(s: Editable, size: Int): CharArray {
        val digits = CharArray(size)
        var index = 0
        var i = 0
        while (i < s.length && index < size) {
            val current = s[i]
            if (Character.isDigit(current)) {
                digits[index] = current
                index++
            }
            i++
        }
        return digits
    }
})

fun String.toIbanOrNull() = try {
    Iban.valueOf(this.replace(" ", ""))
} catch (e: Exception) {
    null
}

fun String.isValidBankCardNumber() = isNotEmpty() && this.matches("^([0-9]{4}-){3}[0-9]{4}$".toRegex())
fun String.isValidBankingIdName() = isNotEmpty() && this.matches("^[0-9a-zA-Z\\s\\.]{2,50}$".toRegex())
fun String.isValidName() = isNotEmpty() && this.matches("^[0-9a-zA-Z\\s\\.]{2,50}$".toRegex())

const val IP_ADDRESS_PATTERN =
    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"

fun String.extractIpAddress(): String? {
    val pattern = Pattern.compile(IP_ADDRESS_PATTERN)
    val matcher = pattern.matcher(this)
    return if (matcher.find()) {
        matcher.group()
    } else {
        null
    }
}

class CurrencyTextWatcher(val currency: Currency, val et: EditText, val liveData: MutableLiveData<BigDecimal?>?) :
    TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        if (s.toString() != current) {
            et.removeTextChangedListener(this)

            val cleanString = s.toString().replace("[$,.]".toRegex(), "").run { if (length > 0) this else "0" }
            val formatted = cleanString.toBigDecimalOrNull()?.format(currency) ?: current

            current = formatted
            et.setText(formatted)
            et.setSelection(formatted.length)

            liveData?.postValue(formatted.replace("[$,.]".toRegex(), "").toBigDecimalOrNull() ?: BigDecimal.ZERO)

            et.addTextChangedListener(this)
        }
    }

    private var current: String = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}
