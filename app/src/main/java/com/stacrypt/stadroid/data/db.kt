package com.stacrypt.stadroid.data

import androidx.room.RoomDatabase
import androidx.room.Database


@Database(
    entities = [
        Asset::class,
        Balance::class
//        Market::class,
//        MarketStatus::class
    ],
    version = 2
)
abstract class StemeraldDatabase : RoomDatabase(){
    abstract val assetDao: AssetDao
    abstract val balanceDao: BalanceDao
}

lateinit var stemeraldDatabase: StemeraldDatabase
