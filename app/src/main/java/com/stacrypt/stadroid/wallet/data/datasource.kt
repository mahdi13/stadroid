package com.stacrypt.stadroid.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class BalanceHistoryDataSourceFactory(
    private val assetName: String
) : DataSource.Factory<Int, BalanceHistory>() {
    val sourceLiveData = MutableLiveData<PageKeyedBalanceHistoryDataSource>()
    override fun create(): DataSource<Int, BalanceHistory> {
        val source = PageKeyedBalanceHistoryDataSource(assetName)
        sourceLiveData.postValue(source)
        return source
    }
}

class PageKeyedBalanceHistoryDataSource(private val assetName: String) :
    PageKeyedDataSource<Int, BalanceHistory>() {

    private val scope = CoroutineScope(Dispatchers.IO)
    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            scope.launch {
                it.invoke()
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, BalanceHistory>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, BalanceHistory>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            try {
                val items = stawalletApiClient.balanceHistory(
                    assetName = assetName,
                    page = params.key
                ).await()
                retry = null
                callback.onResult(items, params.key + 1)
                networkState.postValue(NetworkState.LOADED)
            } catch (e: Exception) {
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(e.message ?: "unknown err"))
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, BalanceHistory>
    ) {
        val request = stawalletApiClient.balanceHistory(assetName = assetName)
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        // triggered by a refresh, we better execute sync
        try {
            val items = runBlocking { request.await() }
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items, null, 1)
        } catch (e: Exception) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(e.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }
}