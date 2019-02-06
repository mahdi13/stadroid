package com.stacrypt.stadroid.data

import androidx.room.RoomDatabase
import androidx.room.Database


@Database(
    entities = [
        Asset::class,
        Balance::class,
        Kline::class,
        Book::class,
        Deal::class,
        Mine::class
//        Market::class,
//        MarketStatus::class
    ],
    version = 4
)
abstract class StemeraldDatabase : RoomDatabase() {
    abstract val assetDao: AssetDao
    abstract val balanceDao: BalanceDao
    abstract val klineDao: KlineDao
    abstract val bookDao: BookDao
    abstract val dealDao: DealDao
    abstract val mineDao: MineDao
}

lateinit var stemeraldDatabase: StemeraldDatabase
