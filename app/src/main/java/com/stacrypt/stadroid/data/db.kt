package com.stacrypt.stadroid.data

import androidx.room.RoomDatabase
import androidx.room.Database


@Database(
    entities = [
        Asset::class,
        Balance::class,
        Kline::class,
        Book::class
//        Market::class,
//        MarketStatus::class
    ],
    version = 3
)
abstract class StemeraldDatabase : RoomDatabase() {
    abstract val assetDao: AssetDao
    abstract val balanceDao: BalanceDao
    abstract val klineDao: KlineDao
    abstract val bookDao: BookDao
}

lateinit var stemeraldDatabase: StemeraldDatabase
