package io.stacrypt.stadroid.ext

import org.kethereum.encodings.decodeBase58WithChecksum
import java.lang.Exception

fun String.isValidBitcoinAddress(testnet: Boolean = false): Boolean {
    if (this.isNullOrEmpty()) return false

    // Check length
    if (!(33..35).contains(length)) return false

    // Check prefix
    when (get(0)) {
        '1' -> if (testnet) return false
        '3' -> if (testnet) return false

        'm' -> if (!testnet) return false
        'n' -> if (!testnet) return false
        '2' -> if (!testnet) return false
    }

    try {
        this.decodeBase58WithChecksum()
    } catch (e: Exception) {
        return false
    }

    return true
}

fun main() {
    // Must be true
    println("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem".isValidBitcoinAddress())
    println("3EktnHQD7RiAE6uzMj2ZifT9YgRrkSgzQX".isValidBitcoinAddress())
    println("mipcBbFg9gMiCh81Kj8tqqdgoZub1ZJRfn".isValidBitcoinAddress(testnet = true))
    println("2MzQwSSnBHWHqSAqtTVQ6v47XtaisrJa1Vc".isValidBitcoinAddress(testnet = true))

    // Must be false
    println("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem".isValidBitcoinAddress(testnet = true))
    println("3EktnHQD7RiAE6uzMj2ZifT9YgRrkSgzQX".isValidBitcoinAddress(testnet = true))
    println("mipcBbFg9gMiCh81Kj8tqqdgoZub1ZJRfn".isValidBitcoinAddress())
    println("2MzQwSSnBHWHqSAqtTVQ6v47XtaisrJa1Vc".isValidBitcoinAddress())
}