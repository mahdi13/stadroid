package com.stacrypt.stadroid

import androidx.room.Entity

@Entity
data class Market(
    val name: String,
    val stock: Long,
    val stockPrec: Long,
    val money: Long,
    val feePrec: Float,
    val minAmount: Long,
    val moneyPrec: Float

)

@Entity
data class MarketStatus(
    val open: Long,  // open price
    val last: Long,  // latest price
    val high: Long,  // highest price
    val low: Long,   //  lowest price
    val deal: Long,  // amount
    val volume: Long // volume
)

@Entity
data class Asset(
    val name: String,
    val prec: String
)

@Entity
data class Balance(
    val assetName: String,
    val available: Double,
    val freeze: Double
)
