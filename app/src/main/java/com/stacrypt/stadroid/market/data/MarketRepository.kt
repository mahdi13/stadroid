package com.stacrypt.stadroid.market.data

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stacrypt.stadroid.data.*
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
                        start = (time.time - DateUtils.WEEK_IN_MILLIS).toInt(),
                        end = time.time.toInt(),
                        interval = (DateUtils.MINUTE_IN_MILLIS * 5).toInt()
                    ).await()
                )
            } catch (e: Exception) {
                // TODO: Try again
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
                )
            } catch (e: Exception) {
                // TODO: Try again
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
            } catch (e: Exception) {
                // TODO: Try again
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
            } catch (e: Exception) {
                // TODO: Try again
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

