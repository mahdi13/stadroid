package com.stacrypt.stadroid.data

import androidx.room.*
import java.util.*

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
    @Embedded var summary: MarketSummary?,
    @Embedded var last: MarketLast?

)

data class MarketStatus(
    val openStatus: Long,
    val highStatus: Long,
    val lowStatus: Long,
    val closeStatus: Long,
    val lastStatus: Long,
    val dealStatus: Long,
    val volumeStatus: Long,
    val periodStatus: Long
)

data class MarketSummary(
    val open24: Long,
    val last24: Long,
    val high24: Long,
    val low24: Long,
    val deal24: Long,
    val volume24: Long
)

data class MarketLast(
    val price: Double
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

@Entity
data class User(
    val id: Int,
    @PrimaryKey val email: String,
    val type: String,
    val isEmailVerified: Boolean,
    val isEvidenceVerified: Boolean,
    val hasSecondFactor: Boolean,
    val isActive: Boolean,
    val invitationCode: String?,
    val createdAt: Date?,
    val modifiedAt: Date?
)

data class TokenResponse(
    val token: String
)

@Entity
data class Ticket(
    @PrimaryKey var id: Int,
    var title: String,
    var memberId: Int?,
    var closedAt: Date?,
    @Embedded(prefix = "dp") var department: TicketDepartment,
    var isClosed: Boolean,
    @Embedded(prefix = "fm") var firstMessage: TicketMessage?,
    var createdAt: Date?,
    var modifiedAt: Date?
)

@Entity(
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = Ticket::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ticketId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TicketMessage(
    var id: Int,
    var text: String,
    var isAnswer: Boolean,
    var ticketId: Int,
    var attachment: String?,
    var memberId: Int?,
    var createdAt: Date?,
    var modifiedAt: Date?
)

data class TicketDepartment(
    var id: Int,
    var title: String
)

@Entity
data class Session(
    @PrimaryKey var id: String,
    var remoteAddress: String?,
    var machine: String?,
    var os: String?,
    var agent: String?,
    var client: String?,
    var app: String?,
    var lastActivity: Date?
)

@Entity
data class SecurityLog(
    @PrimaryKey var id: Int,
    var clientId: Int?,
    var type: String?,
    var details: String?,
    var createdAt: Date?
)

@Entity
data class ShetabAddress(
    @PrimaryKey var id: Int,
    var clientId: Int?,
    var isVerified: Boolean,
    var address: String?,
    var error: String?,
    var createdAt: Date?
)

@Entity
data class ShebaAddress(
    @PrimaryKey var id: Int,
    var clientId: Int?,
    var isVerified: Boolean,
    var address: String?,
    var error: String?,
    var createdAt: Date?
)

data class ErrorResponse(
    val message: String,
    val description: String
)
