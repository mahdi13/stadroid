package com.stacrypt.stadroid.data

import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*


@Database(
    entities = [
        Market::class,
        Asset::class,
        Balance::class,
        Kline::class,
        Book::class,
        Deal::class,
        Mine::class,

        User::class,
        Ticket::class,
        TicketMessage::class,
        SecurityLog::class,
        Session::class,
        ShetabAddress::class,
        ShebaAddress::class
    ],
    version = 5
)
@TypeConverters(Converters::class)
abstract class StemeraldDatabase : RoomDatabase() {
    abstract val marketDao: MarketDao
    abstract val assetDao: AssetDao
    abstract val balanceDao: BalanceDao
    abstract val klineDao: KlineDao
    abstract val bookDao: BookDao
    abstract val dealDao: DealDao
    abstract val mineDao: MineDao
    abstract val userDao: UserDao
}

lateinit var stemeraldDatabase: StemeraldDatabase

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
