package io.stacrypt.stadroid.ui

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Currency

fun Currency.iconResource(): Int? = when (symbol) {
    "BTC" -> R.drawable.ic_btc
    "TBTC" -> R.drawable.ic_btc
    "ETH" -> R.drawable.ic_eth
    "TETH" -> R.drawable.ic_eth
    "IRR" -> R.drawable.ic_irr
    "TIRR" -> R.drawable.ic_irr
    "TRY" -> R.drawable.ic_try
    "TTRY" -> R.drawable.ic_try
    "USD" -> R.drawable.ic_usd
    "TUSD" -> R.drawable.ic_usd
    "EUR" -> R.drawable.ic_eur
    "TEUR" -> R.drawable.ic_eur
    else -> R.drawable.ic_btc
}