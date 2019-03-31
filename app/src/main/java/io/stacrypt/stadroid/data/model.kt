package io.stacrypt.stadroid.data

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Market(
    @PrimaryKey var name: String,
    var stock: String,
    var stockPrec: Int,
    var money: String,
    var feePrec: Int,
    var minAmount: BigDecimal,
    var moneyPrec: Int,
    var buyAmountMin: BigDecimal,
    var buyAmountMax: BigDecimal,
    var sellAmountMin: BigDecimal,
    var sellAmountMax: BigDecimal,
    var takerCommissionRate: String?,
    var makerCommissionRate: String?,
    var baseCurrencySymbol: String?,
    var quoteCurrencySymbol: String?
//    @Embedded var status: MarketStatus?,
//    @Embedded var summary: MarketSummary?,
//    @Embedded var last: MarketLast?

)

data class MarketStatus(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
    val deal: BigDecimal,
    val last: BigDecimal,
    val period: Long
)

data class MarketSummary(
    val name: String,
    val askAmount: BigDecimal,
    val bidAmount: BigDecimal,
    val askCount: Int,
    val bidCount: Int
)

data class MarketLast(
    val price: BigDecimal
)

@Entity
data class Asset(
    @PrimaryKey var name: String,
    var prec: String
)

@Entity
data class Currency(
    var name: String,
    @PrimaryKey var symbol: String,
    var type: String, // fiat / cryptocurrency
    var divideByTen: Int,

    /**
     * Just for crypto:
     */
    var depositMin: BigDecimal?,
    var depositMax: BigDecimal?,
    var withdrawMin: BigDecimal?,
    var withdrawMax: BigDecimal?,
    var depositMaxCommission: BigDecimal?,
    var withdrawMaxCommission: BigDecimal?,
    var depositStaticCommission: BigDecimal?,
    var withdrawStaticCommission: BigDecimal?,
    var depositCommissionRate: String?,
    var withdrawCommissionRate: String?,
    var walletId: String?,
    var normalizationScale: Int,
    var smallestUnitScale: Int

)

data class Kline(
    var market: String,
    var time: Long,
    var o: BigDecimal,
    var h: BigDecimal,
    var l: BigDecimal,
    var c: BigDecimal,
    var volume: BigDecimal,
    var amount: BigDecimal
)

data class Book(
    var market: String,
    var i: Int,
    var side: String,
    var price: BigDecimal,
    var amount: BigDecimal
)

data class Depth(
    var asks: ArrayList<DepthRecord>,
    var bids: ArrayList<DepthRecord>
)

data class DepthRecord(
    var price: BigDecimal,
    var amount: BigDecimal
)

data class Deal(
    var market: String,
    var id: Int,
    var time: Long,
    var type: String,
    var amount: BigDecimal,
    var price: BigDecimal
)

data class Mine(
    var id: Int,
    var market: String,
    var user: String,
    var time: Long,
    var side: String,
    var role: String,
    var amount: BigDecimal,
    var price: BigDecimal,
    var fee: BigDecimal,
    var fillId: Int,
    var orderId: Int
)

@Entity
data class BalanceOverview(
    @PrimaryKey @SerializedName("name") var assetName: String,
    var available: BigDecimal,
    var freeze: BigDecimal,
    @Embedded(prefix = "currency") var currency: Currency
)

data class BalanceHistory(
    @SerializedName("time") var time: Date?,
    @SerializedName("asset") var assetName: String,
    @SerializedName("business") var business: String?,
    @SerializedName("change") var change: BigDecimal,
    @SerializedName("balance") var balance: BigDecimal,
    @SerializedName("detail") var detail: Any?,
    @SerializedName("currency") var currency: Currency
)

data class DepositInfo(
    @SerializedName("id") var id: String,
    @SerializedName("user") var user: String,
    @SerializedName("extra") var extra: String?,
    @SerializedName("creation") var creation: Date,
    @SerializedName("expiration") var expiration: Date?,
    @SerializedName("address") var address: String
)

data class Withdaraw(
    @SerializedName("id") var id: String,
    @SerializedName("user") var user: String,
    @SerializedName("toAddress") var toAddress: String?,
    @SerializedName("netAmount") var netAmount: BigDecimal?,
    @SerializedName("netAmount") var grossAmount: BigDecimal?,
    @SerializedName("estimatedNetworkFee") var estimatedNetworkFee: BigDecimal?,
    @SerializedName("finalNetworkFee") var finalNetworkFee: BigDecimal?,
    @SerializedName("type") var type: String?,
    @SerializedName("isManual") var isManual: Boolean?,
    @SerializedName("status") var status: String?,
    @SerializedName("txid") var txid: String?,
    @SerializedName("txHash") var txHash: String?,
    @SerializedName("blockHeight") var blockHeight: Long?,
    @SerializedName("blockHash") var blockHash: String?,
    @SerializedName("link") var link: String?,
    @SerializedName("confirmationsLeft") var confirmationsLeft: Int?,
    @SerializedName("error") var error: Int?,
    @SerializedName("issuedAt") var creation: Date,
    @SerializedName("paidAt") var expiration: Date?
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

enum class Gender { MALE, FEMALE }

data class Evidence(
    val clientId: Int,
    val cityId: Int?,
    val type: String,
    val gender: Gender?,
    val birthday: Date?,
    val mobilePhone: String?,
    val fixedPhone: String?,
    val address: String?,
    val createdAt: Date?,
    val modifiedAt: Date?,
    val firstName: String?,
    val lastName: String?,
    val nationalCode: String?,
    val error: String?,
    val idCard: String?,
    val idCardSecondary: String?
)

data class Order(
    val id: Int,
    val createdAt: Date?,
    val modifiedAt: Date?,
    val finishedAt: Date?,
    val market: String,
    val user: Int,
    val type: String,
    val side: String,
    val amount: BigDecimal,
    val price: BigDecimal?,
    val takerFeeRate: String?,
    val makerFeeRate: String?,
    val source: String?,
    val filledMoney: BigDecimal?,
    val filledStock: BigDecimal?,
    val filledFee: BigDecimal?
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

data class Session(
    var id: String,
    var remoteAddress: String?,
    var machine: String?,
    var os: String?,
    var agent: String?,
    var client: String?,
    var app: String?,
    var lastActivity: Date?
)

data class SecurityLog(
    var id: Int,
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

private enum class ContentStatus {
    LOADING,
    LOADED,
    COMPLETED,
    FAILED;
}

private enum class ReliabilityStatus {
    LIVE,
    CACHE,
    NONE;
}

private enum class NetworkStatus {
    REALTIME,
    ONLINE,
    OFFLINE,
    ERROR;
}

@Suppress("DataClassPrivateConstructor")
data class LoadingState private constructor(
    val content: ContentStatus,
    val reliability: ReliabilityStatus,
    val network: NetworkStatus,
    val msg: String? = null
) {
//    companion object {
//        val LOADED = NetworkState(NetworkStatus.SUCCESS)
//        val LOADING = NetworkState(NetworkStatus.RUNNING)
//        fun error(msg: String?) = NetworkState(NetworkStatus.FAILED, msg)
//    }
//
//    override fun toString(): String =
//        when (status) {
//            NetworkStatus.RUNNING -> "Loading ..."
//            NetworkStatus.SUCCESS -> "Loading Completed!"
//            NetworkStatus.FAILED -> "Error Loading! Try Again!"
//        }
}

enum class BankIdType { ACCOUNT, CARD }

interface BankId {
    val id: Int
    val clientId: Int
    val isVerified: Int
    val error: Int
    val fiatSymbol: String
    val type: BankIdType
}

data class BankAccount(
    override val id: Int,
    override val clientId: Int,
    override val isVerified: Int,
    override val error: Int,
    override val fiatSymbol: String,
    override val type: BankIdType = BankIdType.ACCOUNT,
    val iban: String,
    val owner: String,
    val bic: String
) : BankId

data class BankCard(
    override val id: Int,
    override val clientId: Int,
    override val isVerified: Int,
    override val error: Int,
    override val fiatSymbol: String,
    override val type: BankIdType = BankIdType.CARD,
    val pan: String,
    val holder: String
) : BankId

@Entity
data class PaymentGateway(
    @PrimaryKey val name: String,
    val cashinMin: BigDecimal,
    val cashoutStaticCommission: BigDecimal,
    val cashinMax: BigDecimal,
    val cashoutMax: BigDecimal,
    val cashinMaxCommission: BigDecimal,
    val cashinStaticCommission: BigDecimal,
    val cashinCommissionRate: String,
    val cashoutMaxCommission: BigDecimal,
    val cashoutCommissionRate: String,
    val cashoutMin: BigDecimal,
    val fiatSymbol: String,
    @Embedded(prefix = "fiat_") val fiat: Fiat
)

data class BankingTransaction(
    @SerializedName("id") var id: Int,
    @SerializedName("memberId") var userId: Int,
    @SerializedName("member") var user: User,
    @SerializedName("fiatSymbol") var fiatSymbol: String,
    @SerializedName("referenceId") var referenceId: String?,
    @SerializedName("amount") var amount: BigDecimal?,
    @SerializedName("commission") var commission: BigDecimal?,
    @SerializedName("creation") var creation: Date,
    @SerializedName("createdAt") var createdAt: Date?,
    @SerializedName("modifiedAt") var modifiedAt: Date?,
    @SerializedName("bankingId") var bankingId: Any?,
    @SerializedName("paymentGateway") var paymentGateway: PaymentGateway?,
    @SerializedName("paymentGatewayName") var paymentGatewayName: String?,
    @SerializedName("error") var error: String?,
    @SerializedName("transactionId") var transactionId: String?,
    @SerializedName("type") var type: String
)

data class Fiat(
    val name: String,
    val type: String,
    val symbol: String,
    val divideByTen: Int
)

val dateFormatter by lazy { SimpleDateFormat("dd MMM yyyy HH:mm") }
fun Date.format() = dateFormatter.format(this)