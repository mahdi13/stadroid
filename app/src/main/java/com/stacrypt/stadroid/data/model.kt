package com.stacrypt.stadroid.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Market(
    @PrimaryKey var name: String,
    var stock: String,
    var stockPrec: Int,
    var money: String,
    var feePrec: Int,
    var minAmount: String,
    var moneyPrec: Int,
    @Embedded var status: MarketStatus?,
    @Embedded var summary: MarketSummary?

)

data class MarketStatus(
    val open: Long,
    val high: Long,
    val low: Long,
    val close: Long,
    val last: Long,
    val deal: Long,
    val volume: Long,
    val period: Long
)

data class MarketSummary(
    val open: Long,
    val last: Long,
    val high: Long,
    val low: Long,
    val deal: Long,
    val volume: Long
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
    primaryKeys = ["market", "id"]
)
data class Deal(
    var market: String,
    var id: Int,
    var time: Long,
    var type: String,
    var amount: String,
    var price: String
)

@Entity(
    primaryKeys = ["market", "id"]
)
data class Mine(
    var id: Int,
    var market: String,
    var user: String,
    var time: Long,
    var side: String,
    var role: String,
    var amount: String,
    var price: String,
    var fee: String,
    var fillId: Int,
    var orderId: Int
)

@Entity
data class Balance(
    @PrimaryKey var assetName: String,
    var available: Double,
    var freeze: Double
)

data class User(
    val id: Int,
    val email: String
)

data class TokenResponse(
    val token: String
)

data class ErrorResponse(
    val message: String,
    val description: String
)
