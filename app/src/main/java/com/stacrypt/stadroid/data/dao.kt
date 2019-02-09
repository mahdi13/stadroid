package com.stacrypt.stadroid.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update


@Dao
interface MarketDao {
    @Insert(onConflict = REPLACE)
    fun save(market: Market)

    @Update(onConflict = REPLACE)
    fun update(market: Market)

    @Query("SELECT * FROM Market WHERE name = :name")
    fun load(name: String): LiveData<Market>

    @Query("SELECT * FROM Market ORDER BY name ASC")
    fun loadAll(): LiveData<List<Market>>
}


@Dao
interface AssetDao {
    @Insert(onConflict = REPLACE)
    fun save(asset: Asset)

    @Query("SELECT * FROM Asset")
    fun loadAll(): LiveData<List<Asset>>
}

@Dao
interface BalanceDao {
    @Insert(onConflict = REPLACE)
    fun save(balance: Balance)

    @Query("SELECT * FROM Balance")
    fun loadAll(): LiveData<List<Balance>>
}

@Dao
interface KlineDao {
    @Insert(onConflict = REPLACE)
    fun save(kline: Kline)

    @Query("SELECT * FROM Kline WHERE market = :market ORDER BY time DESC LIMIT 10000")
    fun loadByMarket(market: String): LiveData<List<Kline>>
}

@Dao
interface BookDao {
    @Insert(onConflict = REPLACE)
    fun save(book: Book)

    @Query("SELECT * FROM Book WHERE market = :market ORDER BY i ASC")
    fun loadByMarket(market: String): LiveData<List<Book>>
}

@Dao
interface DealDao {
    @Insert(onConflict = REPLACE)
    fun save(deal: Deal)

    @Query("SELECT * FROM Deal WHERE market = :market ORDER BY time DESC")
    fun loadByMarket(market: String): LiveData<List<Deal>>
}

@Dao
interface MineDao {
    @Insert(onConflict = REPLACE)
    fun save(mine: Mine)

    @Query("SELECT * FROM Mine WHERE market = :market ORDER BY time DESC")
    fun loadByMarket(market: String): LiveData<List<Mine>>
}
