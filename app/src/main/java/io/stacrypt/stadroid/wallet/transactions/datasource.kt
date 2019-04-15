package io.stacrypt.stadroid.wallet.transactions

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import io.stacrypt.stadroid.data.BankingTransaction
import io.stacrypt.stadroid.data.DepositDetail
import io.stacrypt.stadroid.data.NetworkState
import io.stacrypt.stadroid.data.Withdraw
import io.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DepositHistoryDataSourceFactory(
    private val assetName: String
) : DataSource.Factory<Int, DepositDetail>() {
    val sourceLiveData = MutableLiveData<PagedCryptocurrencyDepositHistoryDataSource>()
    override fun create(): DataSource<Int, DepositDetail> {
        val source = PagedCryptocurrencyDepositHistoryDataSource(assetName)
        sourceLiveData.postValue(source)
        return source
    }
}

class WithdrawHistoryDataSourceFactory(
    private val assetName: String
) : DataSource.Factory<Int, Withdraw>() {
    val sourceLiveData = MutableLiveData<PagedCryptocurrencyWithdrawHistoryDataSource>()
    override fun create(): DataSource<Int, Withdraw> {
        val source = PagedCryptocurrencyWithdrawHistoryDataSource(assetName)
        sourceLiveData.postValue(source)
        return source
    }
}

class BankingTransactionHistoryDataSourceFactory(
    private val type: String?,
    private val assetName: String?
) : DataSource.Factory<Int, BankingTransaction>() {
    val sourceLiveData = MutableLiveData<PagedBankingTransactionHistoryDataSource>()
    override fun create(): DataSource<Int, BankingTransaction> {
        val source = PagedBankingTransactionHistoryDataSource(type, assetName)
        sourceLiveData.postValue(source)
        return source
    }
}

class PagedCryptocurrencyDepositHistoryDataSource(private val assetName: String) :
    PageKeyedDataSource<Int, DepositDetail>() {

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
        callback: LoadCallback<Int, DepositDetail>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DepositDetail>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            try {
//                val items = stemeraldApiClient.balanceHistory(
                val items = stemeraldApiClient.getDeposits(
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
        callback: LoadInitialCallback<Int, DepositDetail>
    ) {
//        val request = stemeraldApiClient.balanceHistory(assetName = assetName)
        val request = stemeraldApiClient.getDeposits(assetName = assetName)
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

class PagedCryptocurrencyWithdrawHistoryDataSource(private val assetName: String) :
    PageKeyedDataSource<Int, Withdraw>() {

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
        callback: LoadCallback<Int, Withdraw>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Withdraw>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            try {
//                val items = stemeraldApiClient.balanceHistory(
                val items = stemeraldApiClient.getWithdraws(
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
        callback: LoadInitialCallback<Int, Withdraw>
    ) {
//        val request = stemeraldApiClient.balanceHistory(assetName = assetName)
        val request = stemeraldApiClient.getWithdraws(assetName = assetName)
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

class PagedBankingTransactionHistoryDataSource(
    private val type: String?,
    private val assetName: String?
) : PageKeyedDataSource<Int, BankingTransaction>() {

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
        callback: LoadCallback<Int, BankingTransaction>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, BankingTransaction>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            try {
//                val items = stemeraldApiClient.balanceHistory(
                val items = stemeraldApiClient.getBankingTransactions(
                    type = type,
                    fiatSymbol = assetName,
                    take = params.requestedLoadSize,
                    skip = params.key * params.requestedLoadSize
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
        callback: LoadInitialCallback<Int, BankingTransaction>
    ) {
//        val request = stemeraldApiClient.balanceHistory(assetName = assetName)
        val request = stemeraldApiClient.getBankingTransactions(
            type = type,
            fiatSymbol = assetName,
            take = params.requestedLoadSize,
            skip = 0
        )
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

