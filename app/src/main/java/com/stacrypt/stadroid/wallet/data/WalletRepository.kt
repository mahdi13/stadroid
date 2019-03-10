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
import retrofit2.HttpException

// TODO: Add rate limiter
object WalletRepository {
    private val assetDao = stemeraldDatabase.assetDao
    private val balanceOverviewDao = stemeraldDatabase.balanceOverviewDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getDepositInfo(assetName: String): LiveData<DepositInfo?> {
        val liveData = MutableLiveData<DepositInfo?>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.showDepositInfo(assetName = assetName).await())
            } catch (e: HttpException) {
                if (e.code() == 404) { // TODO: More strict if clause (or tell the backend team to handle it there!)
                    renewDepositInfo(assetName, liveData)
                } else {
                    // TODO Show error
                    liveData.postValue(null)
                }
            } catch (e: Exception) {
                // TODO Show error
                liveData.postValue(null)
            } finally {
            }
        }
        return liveData
    }

    fun renewDepositInfo(assetName: String, depositInfo: MutableLiveData<DepositInfo?>): LiveData<DepositInfo?> {
        scope.launch {
            try {
                depositInfo.postValue(stemeraldApiClient.renewDepositInfo(assetName = assetName).await())
            } catch (e: Exception) {
                // TODO Show error
                depositInfo.postValue(null)
            } finally {
            }
        }
        return depositInfo
    }

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
            pageSize = 20 // Doesn't matter, because server will set it
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
            //            stemeraldApiClient.balanceOverview().await().forEach { balanceOverviewDao.save(it) }
            mockStemeraldApiClient.balanceList().await().apply {
                balanceOverviewDao.deleteAll() // FIXME
            }.forEach { balanceOverviewDao.save(it) } // FIXME
        }
    }

    private fun refreshAssets() {
        job = scope.launch {
            //            stemeraldApiClient.assetList().await().forEach { assetDao.save(it) }
            mockStemeraldApiClient.assetList().await().forEach { assetDao.save(it) } // FIXME
        }
    }

}

