package io.stacrypt.stadroid.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class ListingDataSourceFactory<T> : DataSource.Factory<Int, T>() {
    val sourceLiveData = MutableLiveData<Any>()
    override fun create(): DataSource<Int, T> {
        sourceLiveData.postValue(source)
        return source
    }

    abstract suspend fun load(page: Int, pageSize: Int): List<T>

    val source by lazy {
        object : ListingPageKeyedDataSource<T>() {
            private val scope = CoroutineScope(Dispatchers.IO)
            // keep a function reference for the retry event
            private var retry: (() -> Any)? = null

            /**
             * There is no sync on the state because paging will always call loadInitial first then wait
             * for it to return some success value before calling loadAfter.
             */
            override val networkState = MutableLiveData<NetworkState>()

            override val initialLoad = MutableLiveData<NetworkState>()

            override fun retryAllFailed() {
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
                callback: LoadCallback<Int, T>
            ) {
                // ignored, since we only ever append to our initial load
            }

            override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
                networkState.postValue(NetworkState.LOADING)
                scope.launch {
                    try {
                        val items = load(params.key, params.requestedLoadSize)
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
                callback: LoadInitialCallback<Int, T>
            ) {
                networkState.postValue(NetworkState.LOADING)
                initialLoad.postValue(NetworkState.LOADING)

                // triggered by a refresh, we better execute sync
                try {
                    val items = runBlocking {
                        load(0, params.requestedLoadSize)
                    }
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
    }
}

abstract class ListingPageKeyedDataSource<T> : PageKeyedDataSource<Int, T>() {
    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    abstract val networkState: MutableLiveData<NetworkState>

    abstract val initialLoad: MutableLiveData<NetworkState>

    abstract fun retryAllFailed()
}
