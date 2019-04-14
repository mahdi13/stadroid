package io.stacrypt.stadroid.market.data

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.PullerLiveData
import io.stacrypt.stadroid.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

object MarketRepository {
    private val marketDao: MarketDao = stemeraldDatabase.marketDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * This method just return the basic data of one market, so we'll call it frequently. So we will store it
     * in the DB.
     *
     * TODO: Limit webservice call rate
     */
    fun getMarket(marketName: String): LiveData<Market> {
//        refreshMarkets() // FIXME: Uncommenting this line will cause flashing (and loading loop) of vitrine fragment
        // FIXME: But also we should find a way to update the market data periodically
        return marketDao.load(marketName)
    }

    /**
     * This method just return the basic data of each market, so we'll call it frequently. So we will store it
     * in the DB.
     *
     * TODO: Limit webservice call rate
     */
    fun getMarkets(): LiveData<List<Market>> {
        refreshMarkets()
        return marketDao.loadAll()
    }

    fun getKline(market: String): LiveData<List<Kline>> {
        val time = Calendar.getInstance().time
        return getKline(
            market = market,
            start = (time.time - DateUtils.MINUTE_IN_MILLIS * 60).div(1000L).toInt(), // FIXME
            end = time.time.div(1000L).toInt(), // FIXME
            interval = (DateUtils.MINUTE_IN_MILLIS).div(1000L).toInt()
        )
    }

    fun getKline48(market: String): LiveData<List<Kline>> {
        val time = Calendar.getInstance().time
        return getKline(
            market = market,
            start = (time.time - DateUtils.HOUR_IN_MILLIS * 48).div(1000L).toInt(), // FIXME
            end = time.time.div(1000L).toInt(), // FIXME
            interval = (DateUtils.HOUR_IN_MILLIS).div(1000L).toInt()
        )
    }

    fun getKline24(market: String): LiveData<List<Kline>> {
        val time = Calendar.getInstance().time
        return getKline(
            market = market,
            start = (time.time - DateUtils.HOUR_IN_MILLIS * 24).div(1000L).toInt(), // FIXME
            end = time.time.div(1000L).toInt(), // FIXME
            interval = (DateUtils.HOUR_IN_MILLIS).div(1000L).toInt()
        )
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    private fun getKline(market: String, start: Int, end: Int, interval: Int): LiveData<List<Kline>> =
        PullerLiveData<List<Kline>>(scope, 1000) {
            return@PullerLiveData stemeraldApiClient.kline(
                market = market,
                start = start,
                end = end,
                interval = interval
            ).await()
        }
//         MutableLiveData<List<Kline>>().apply {
//             val j = scope.launch {
//                 do {
//                     if (hasActiveObservers())
//                         try {
//                             postValue(
//                                 stemeraldApiClient.kline(
//                                     market = market,
//                                     start = start,
//                                     end = end,
//                                     interval = interval
//                                 ).await()
// //                    mockStemeraldApiClient.kline(
// //                        market = market
// //                    ).await()
//                             )
//                         } catch (e: Exception) {
//                             // TODO: Try again
//                             e.printStackTrace()
//                         } finally {
//                             Log.e("salam updating", this.toString())
//                         }
//
//                     Log.e("salam", this.toString())
//                     delay(1000)
//                     // if (!this@apply.hasActiveObservers()) this.cancel()
//                 } while (true)
//             }
//             // observeForever {  }
//         }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getBook(market: String): LiveData<BookResponse> = PullerLiveData<BookResponse>(scope, 1000) {
        BookResponse(
            buys = stemeraldApiClient.book(market, "buy").await(),
            sells = stemeraldApiClient.book(market, "sell").await()
        )
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMyDeals(market: String): LiveData<List<MyDeal>> = PullerLiveData<List<MyDeal>>(scope, 1000) {
        stemeraldApiClient.getMyDeals(market = market, take = 50, skip = 0).await()
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketDeals(market: String): LiveData<List<MarketDeal>> = PullerLiveData<List<MarketDeal>>(scope, 1000) {
        stemeraldApiClient.getMarketDeals(market = market, take = 50, lastId = 0).await()
    }

    fun getMyActiveOrders(market: String): LiveData<List<Order>> = getOrders(market, "pending")

    fun getMyFinishedOrders(market: String): LiveData<List<Order>> = getOrders(market, "finished")

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    private fun getOrders(market: String, status: String): LiveData<List<Order>> =
        PullerLiveData<List<Order>>(scope, 1000) {
            stemeraldApiClient.getOrders(marketName = market, limit = 50, offset = 0, status = status).await()
        }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketSummary(market: String): LiveData<MarketSummary> = PullerLiveData<MarketSummary>(scope, 1000) {
        stemeraldApiClient.marketSummary(market = market).await()[0]
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketStatus(market: String): LiveData<MarketStatus> = PullerLiveData<MarketStatus>(scope, 1000) {
        stemeraldApiClient.marketStatus(market = market).await()
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketLast(market: String): LiveData<MarketLast> = PullerLiveData<MarketLast>(scope, 1000) {
        stemeraldApiClient.marketLast(market = market).await()
    }

    private fun refreshMarkets() {
        scope.launch {
            try {
                stemeraldApiClient.marketList().await().forEach {
                    MarketRepository.marketDao.save(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO Report
            }
        }
    }
}
