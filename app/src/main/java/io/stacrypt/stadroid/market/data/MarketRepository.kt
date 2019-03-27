package io.stacrypt.stadroid.market.data

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stacrypt.stadroid.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
        refreshMarkets()
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

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getKline(market: String): LiveData<List<Kline>> {
        val liveData = MutableLiveData<List<Kline>>()
        scope.launch {
            try {
                val time = Calendar.getInstance().time
                liveData.postValue(
                    stemeraldApiClient.kline(
                        market = market,
                        start = (time.time - DateUtils.MINUTE_IN_MILLIS).div(1000L).toInt(), //FIXME
                        end = time.time.div(1000L).toInt(), // FIXME
                        interval = (DateUtils.MINUTE_IN_MILLIS * 5).div(1000L).toInt()
                    ).await()
//                    mockStemeraldApiClient.kline(
//                        market = market
//                    ).await()
                )
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getBook(market: String): LiveData<BookResponse> {
        val liveData = MutableLiveData<BookResponse>()
        scope.launch {
            try {
                liveData.postValue(
                    BookResponse(
                        buys = stemeraldApiClient.book(market, "buy").await(),
                        sells = stemeraldApiClient.book(market, "sell").await()
                    )
//                    mockStemeraldApiClient.book(market).await()
                )
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getDeal(market: String): LiveData<List<Deal>> {
        val liveData = MutableLiveData<List<Deal>>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.deal(market).await())
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMineOverview(market: String): LiveData<List<Mine>> {
        val liveData = MutableLiveData<List<Mine>>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.mine(market = market, take = 50, skip = 0).await())
//                liveData.postValue(mockStemeraldApiClient.mine(market = market).await())
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    fun getMyActiveOrders(market: String): LiveData<List<Order>> = getOrders(market, "pending")

    fun getMyFinishedOrders(market: String): LiveData<List<Order>> = getOrders(market, "finished")

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    private fun getOrders(market: String, status: String): LiveData<List<Order>> {
        val liveData = MutableLiveData<List<Order>>()
        scope.launch {
            try {
                liveData.postValue(
                    stemeraldApiClient.getOrders(
                        marketName = market,
                        limit = 50,
                        offset = 0,
                        status = status
                    ).await()
                )
//                liveData.postValue(mockStemeraldApiClient.mine(market = market).await())
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketSummary(market: String): LiveData<MarketSummary> {
        val liveData = MutableLiveData<MarketSummary>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.marketSummary(market = market).await()[0])
//                liveData.postValue(
//                    MarketSummary(
//                        open24 = 4301, last24 = 4598, high24 = 5034, low24 = 3990, deal24 = 3232312, volume24 = 42345656
//                    )
//                )
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketStatus(market: String): LiveData<MarketStatus> {
        val liveData = MutableLiveData<MarketStatus>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.marketStatus(market = market).await())
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
    }

    /**
     * In memory, because cached data is unusable.
     *
     * TODO: Update it automatically
     */
    fun getMarketLast(market: String): LiveData<MarketLast> {
        val liveData = MutableLiveData<MarketLast>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.marketLast(market = market).await())
            } catch (e: Exception) {
                // TODO: Try again
                e.printStackTrace()
            }
        }
        return liveData
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

