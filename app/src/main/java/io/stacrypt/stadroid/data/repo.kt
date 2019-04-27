package io.stacrypt.stadroid.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import kotlinx.coroutines.*
import retrofit2.HttpException

fun <T> ListingDataSourceFactory<T>.listing(): Listing<T> {

    val livePagedList = toLiveData(
        pageSize = 20 // Doesn't matter, because server will set it
    )

    val refreshState = Transformations.switchMap(sourceLiveData) {
        (it as ListingPageKeyedDataSource<T>).initialLoad
    }

    return Listing(
        pagedList = livePagedList,
        networkState = Transformations.switchMap(sourceLiveData) {
            (it as ListingPageKeyedDataSource<T>).networkState
        },
        retry = {
            (sourceLiveData.value as ListingPageKeyedDataSource<T>?)?.retryAllFailed()
        },
        refresh = {
            (sourceLiveData.value as ListingPageKeyedDataSource<T>?)?.invalidate()
        },
        refreshState = refreshState
    )
}

object UserRepository {
    private val userDao: UserDao = stemeraldDatabase.userDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun getMe(): LiveData<User> {
        UserRepository.refreshMe()
        return UserRepository.userDao.loadByEmail(sessionManager.getPayload().email)
    }

    fun getMyEvidences(): LiveData<Evidence> {
        val liveData = MutableLiveData<Evidence>()
        job = scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.getMyEvidences().await())
            } catch (e: Exception) {
                // TODO: Show error
                e.printStackTrace()
            }
        }
        return liveData
    }

    private fun refreshMe() {
        UserRepository.scope.launch {
            stemeraldApiClient.me().await().let {
                UserRepository.userDao.save(it)
            }
        }
    }

    fun getCountries(): LiveData<List<Country>> {
        val liveData = MutableLiveData<List<Country>>()
        job = scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.getCountries().await())
            } catch (e: Exception) {
                // TODO: Show error
                e.printStackTrace()
            }
        }
        return liveData
    }

    fun getStates(countryId: Int): LiveData<List<State>> {
        val liveData = MutableLiveData<List<State>>()
        job = scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.getStates(countryId).await())
            } catch (e: Exception) {
                // TODO: Show error
                e.printStackTrace()
            }
        }
        return liveData
    }

    fun getCities(stateId: Int): LiveData<List<City>> {
        val liveData = MutableLiveData<List<City>>()
        job = scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.getCities(stateId).await())
            } catch (e: Exception) {
                // TODO: Show error
                e.printStackTrace()
            }
        }
        return liveData
    }
}

object SecurityRepository {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // /**
    //  * Always online
    //  */
    // fun getSecurityLogs(): Listing<SecurityLog> {
    //     val sourceFactory = NotificationDataSourceFactory()
    //
    //     val livePagedList = sourceFactory.toLiveData(pageSize = 20)
    //
    //     val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
    //         it.initialLoad
    //     }
    //
    //     return Listing(
    //         pagedList = livePagedList,
    //         networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
    //             it.networkState
    //         },
    //         retry = {
    //             sourceFactory.sourceLiveData.value?.retryAllFailed()
    //         },
    //         refresh = {
    //             sourceFactory.sourceLiveData.value?.invalidate()
    //         },
    //         refreshState = refreshState
    //     )
    // }

    /**
     * Always online
     */
    fun getLastLogin(): LiveData<SecurityLog?> {
        val liveData = MutableLiveData<SecurityLog?>()
        scope.launch {
            try {
                liveData.postValue(stemeraldApiClient.getSecurityLogs(take = 1, type = "login").await().firstOrNull())
            } catch (e: HttpException) {
                // TODO Show error or retry
                liveData.postValue(null)
                e.printStackTrace()
//                }
            } catch (e: Exception) {
                // TODO Show error or retry
                liveData.postValue(null)
                e.printStackTrace()
            } finally {
            }
        }
        return liveData
    }
}

// // A generic class that contains data and status about loading this data.
// class Resource<T> private constructor(val status: Status, val data: T?, val message: String?) {
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
// }
//
// // ResultType: Type for the Resource data.
// // RequestType: Type for the API response.
// abstract class NetworkBoundResource<ResultType, RequestType> {
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
// }
