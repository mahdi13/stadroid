package com.stacrypt.stadroid.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*


object MarketRepository {
    private val marketDao: MarketDao = stemeraldDatabase.marketDao
    private val klineDao: KlineDao = stemeraldDatabase.klineDao
    private val bookDao: BookDao = stemeraldDatabase.bookDao
    private val dealDao: DealDao = stemeraldDatabase.dealDao
    private val mineDao: MineDao = stemeraldDatabase.mineDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun getMarkets(): LiveData<List<Market>> {
        refreshMarkets()
        return marketDao.loadAll()
    }

    fun getKline(market: String): LiveData<List<Kline>> {
        refreshKline()
        return klineDao.loadByMarket(market)
    }

    fun getBook(market: String): LiveData<List<Book>> {
        refreshBook()
        return bookDao.loadByMarket(market)
    }

    fun getDeal(market: String): LiveData<List<Deal>> {
        refreshDeal()
        return dealDao.loadByMarket(market)
    }

    fun getMine(market: String): LiveData<List<Mine>> {
        refreshMine()
        return mineDao.loadByMarket(market)
    }

    private fun refreshMarkets() {
        MarketRepository.scope.launch {
//            stemeraldApiClient.marketList().await().forEach {
            stemeraldApiClient.marketList().await().firstOrNull()?.let {
                it.status = stemeraldApiClient.marketStatus(it.name).await()
                it.summary = stemeraldApiClient.marketSummary(it.name).await().firstOrNull()
                it.last = stemeraldApiClient.marketLast(it.name).await()
                MarketRepository.marketDao.save(it)
            }
        }
    }

    private fun refreshKline() {
        MarketRepository.scope.launch {
            stemeraldApiClient.kline().await().forEach {
                MarketRepository.klineDao.save(it)
            }
        }
    }

    private fun refreshBook() {
        MarketRepository.scope.launch {
            stemeraldApiClient.book().await().run { buys + sells }.forEach {
                MarketRepository.bookDao.save(it)
            }
        }
    }

    private fun refreshDeal() {
        MarketRepository.scope.launch {
            stemeraldApiClient.deal().await().forEach {
                MarketRepository.dealDao.save(it)
            }
        }
    }

    private fun refreshMine() {
        MarketRepository.scope.launch {
            stemeraldApiClient.mine().await().forEach {
                MarketRepository.mineDao.save(it)
            }
        }
    }

}


object WalletRepository {
    private val assetDao: AssetDao = stemeraldDatabase.assetDao
    private val balanceDao: BalanceDao = stemeraldDatabase.balanceDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    //    fun loadAssets(): NetworkBoundResource<List<Asset>, Asset> = object : NetworkBoundResource<List<Asset>, Asset>() {
//        override val asLiveData: LiveData<Resource<List<Asset>>>
//            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//
//        override fun shouldFetch(data: List<Asset>?): Boolean {
//            return rateLimiter.canFetch(userId)
//                    && (data == null || !isFresh(data));
//        }
//
//        override fun onFetchFailed() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun saveCallResult(asset: Asset) {
//            assetDao!!.save(asset)
//        }
//
//
//        override fun loadFromDb(): LiveData<List<Asset>> {
//            return assetDao!!.loadAll()
//        }
//
//        override fun createCall(): LiveData<ApiResponse<Asset>> {
//            return stemeraldApiClient.assetList().getAsLiveData()
//        }
//    }

    fun getBalances(): LiveData<List<Balance>> {
        refreshBalances()
        return balanceDao.loadAll()
    }

    fun getAsset(): LiveData<List<Asset>> {
//        refreshAssets()
        return assetDao.loadAll()
    }

    private fun refreshBalances() {
        job = scope.launch {
            stemeraldApiClient.balanceList().await().forEach { balanceDao.save(it) }
        }
    }

    private fun refreshAssets() {
        job = scope.launch {
            stemeraldApiClient.assetList().await().forEach { assetDao.save(it) }
        }
    }

}

//// A generic class that contains data and status about loading this data.
//class Resource<T> private constructor(val status: Status, val data: T?, val message: String?) {
//
//    enum class Status {
//        SUCCESS, ERROR, LOADING
//    }
//
//    companion object {
//
//        fun <T> success(data: T): Resource<T> {
//            return Resource(Status.SUCCESS, data, null)
//        }
//
//        fun <T> error(msg: String, data: T?): Resource<T> {
//            return Resource(Status.ERROR, data, msg)
//        }
//
//        fun <T> loading(data: T?): Resource<T> {
//            return Resource(Status.LOADING, data, null)
//        }
//    }
//}
//
//// ResultType: Type for the Resource data.
//// RequestType: Type for the API response.
//abstract class NetworkBoundResource<ResultType, RequestType> {
//
//    // Returns a LiveData object that represents the resource that's implemented
//    // in the base class.
//    abstract val asLiveData: LiveData<Resource<ResultType>>
//
//    // Called to save the result of the API response into the database.
//    @WorkerThread
//    protected abstract fun saveCallResult(item: RequestType)
//
//    // Called with the data in the database to decide whether to fetch
//    // potentially updated data from the network.
//    @MainThread
//    protected abstract fun shouldFetch(data: ResultType?): Boolean
//
//    // Called to get the cached data from the database.
//    @MainThread
//    protected abstract fun loadFromDb(): LiveData<ResultType>
//
//    // Called to create the API call.
//    @MainThread
//    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
//
//    // Called when the fetch fails. The child class may want to reset components
//    // like rate limiter.
//    @MainThread
//    abstract fun onFetchFailed()
//}
