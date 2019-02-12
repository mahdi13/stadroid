package com.stacrypt.stadroid.data

import androidx.room.RoomDatabase
import androidx.room.Database


@Database(
    entities = [
        Market::class,
        Asset::class,
        Balance::class,
        Kline::class,
        Book::class,
        Deal::class,
        Mine::class
    ],
    version = 5
)
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
