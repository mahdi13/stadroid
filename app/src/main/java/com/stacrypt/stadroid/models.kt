package com.stacrypt.stadroid

data class Market(
    val name: String,
    val stock: Long ,
    val stockPrec: Long,
    val money: Long,
    val feePrec: Float,
    val minAmount: Long,
    val moneyPrec: Float

)

data class MarketStatus(
    val open: Long,  // open price
    val last: Long,  // latest price
    val high: Long,  // highest price
    val low: Long,   //  lowest price
    val deal: Long,  // amount
    val volume: Long // volume
)