package com.stacrypt.stadroid.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class Market(
    val name: String,
    val stock: Long,
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

@Entity
data class Asset(
    @PrimaryKey var name: String,
    var prec: String
)

@Entity(
    primaryKeys = ["market", "time"]
)
data class Kline(
    var market: String,
    var time: Long,
    var o: Double,
    var h: Double,
    var l: Double,
    var c: Double,
    var volume: Double,
    var amount: Double
)

@Entity(
    primaryKeys = ["market", "i", "side"]
)
data class Book(
    var market: String,
    var i: Int,
    var side: String,
    var price: String,
    var amount: String
)

@Entity(
//    foreignKeys = [ForeignKey(
//        entity = Asset::class,
//        parentColumns = arrayOf("name"),
//        childColumns = arrayOf("assetName"),
//        onDelete = ForeignKey.CASCADE
//    )]
)
data class Balance(
    @PrimaryKey var assetName: String,
    var available: Double,
    var freeze: Double
)
