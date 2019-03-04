package com.stacrypt.stadroid.wallet.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.stacrypt.stadroid.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// TODO: Add rate limiter
object WalletRepository {
    private val assetDao = stemeraldDatabase.assetDao
    private val balanceOverviewDao = stemeraldDatabase.balanceOverviewDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

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

    /**
     * TODO: Think about online and offline
     * Database first, because of too much query
     * TODO: Schedule network update in particular time durations, again and again
     */
    fun getBalanceOverview(): LiveData<List<BalanceOverview>> {
        refreshBalanceOverview()
        return balanceOverviewDao.loadAll()
    }

    /**
     * TODO: Think about online and offline
     * Database first, because of too much query
     * TODO: Schedule network update in particular time durations, again and again
     */
    fun getBalanceOverview(assetName: String): LiveData<BalanceOverview> {
        refreshBalanceOverview()
        return balanceOverviewDao.load(assetName)
    }

    /**
     * Always online
     */
    fun getBalanceHistory(assetName: String): Listing<BalanceHistory> {
        val sourceFactory = BalanceHistoryDataSourceFactory(assetName)

        val livePagedList = sourceFactory.toLiveData(
            pageSize = 0 // Doesn't matter, because server will set it
        )

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }

        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            refreshState = refreshState
        )
    }

    /**
     * Asset list changes rarely, so we trust the database stored data to load faster.
     * It will be automatically updated if any change happens.
     */
    fun getAssetList(): LiveData<List<Asset>> {
        refreshAssets() // TODO: Execute it rarely
        return assetDao.loadAll()
    }

    /**
     * Asset changes rarely, so we trust the database stored data to load faster.
     * It will be automatically updated if any change happens.
     */
    fun getAsset(assetName: String): LiveData<Asset> {
        refreshAssets()// TODO: Execute it rarely
        return assetDao.loadByName(assetName)
    }


    private fun refreshBalanceOverview() {
        job = scope.launch {
            stawalletApiClient.balanceOverview().await().forEach { balanceOverviewDao.save(it) }
        }
    }

    private fun refreshAssets() {
        job = scope.launch {
            stemeraldApiClient.assetList().await().forEach { assetDao.save(it) }
        }
    }

}

