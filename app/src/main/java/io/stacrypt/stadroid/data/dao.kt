package io.stacrypt.stadroid.data

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

    @Query("SELECT * FROM Asset WHERE name = :name")
    fun loadByName(name: String): LiveData<Asset>
}

@Dao
interface CurrencyDao {
    @Insert(onConflict = REPLACE)
    fun save(currency: Currency)

    @Query("SELECT * FROM Currency")
    fun loadAll(): LiveData<List<Currency>>

    @Query("SELECT * FROM Currency WHERE symbol = :symbol")
    fun loadBySymbol(symbol: String): LiveData<Currency>
}

@Dao
interface BalanceOverviewDao {
    @Insert(onConflict = REPLACE)
    fun save(balanceOverview: BalanceOverview)

    @Query("SELECT * FROM BalanceOverview WHERE assetName = :assetName")
    fun load(assetName: String): LiveData<BalanceOverview>

    @Query("DELETE FROM BalanceOverview")
    fun deleteAll()

    @Query("SELECT * FROM BalanceOverview")
    fun loadAll(): LiveData<List<BalanceOverview>>
}

@Dao
interface PaymentGatewayDao {
    @Insert(onConflict = REPLACE)
    fun save(paymentGateway: PaymentGateway)

    @Query("DELETE FROM PaymentGateway")
    fun deleteAll()

    @Query("SELECT * FROM PaymentGateway")
    fun loadAll(): LiveData<List<PaymentGateway>>

    @Query("SELECT * FROM PaymentGateway WHERE fiatSymbol = :fiatSymbol")
    fun loadByFiatSymbol(fiatSymbol: String): LiveData<List<PaymentGateway>>
}

//@Dao
//interface KlineDao {
//    @Insert(onConflict = REPLACE)
//    fun save(kline: Kline)
//
//    @Query("SELECT * FROM Kline WHERE market = :market ORDER BY time DESC LIMIT 10000")
//    fun loadByMarket(market: String): LiveData<List<Kline>>
//}
//
//@Dao
//interface BookDao {
//    @Insert(onConflict = REPLACE)
//    fun save(book: Book)
//
//    @Query("SELECT * FROM Book WHERE market = :market ORDER BY i ASC")
//    fun loadByMarket(market: String): LiveData<List<Book>>
//}
//
//@Dao
//interface DealDao {
//    @Insert(onConflict = REPLACE)
//    fun save(deal: Deal)
//
//    @Query("SELECT * FROM Deal WHERE market = :market ORDER BY time DESC")
//    fun loadByMarket(market: String): LiveData<List<Deal>>
//}
//
//@Dao
//interface MineDao {
//    @Insert(onConflict = REPLACE)
//    fun save(mine: Mine)
//
//    @Query("SELECT * FROM Mine WHERE market = :market ORDER BY time DESC")
//    fun loadByMarket(market: String): LiveData<List<Mine>>
//}

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Query("SELECT * FROM User WHERE email = :email")
    fun loadByEmail(email: String): LiveData<User>
}
