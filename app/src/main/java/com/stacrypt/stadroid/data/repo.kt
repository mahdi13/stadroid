package com.stacrypt.stadroid.data

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import java.util.*


object UserRepository {
    private val userDao: UserDao = stemeraldDatabase.userDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun getMe(): LiveData<User> {
        UserRepository.refreshMe()
        return UserRepository.userDao.loadByEmail(sessionManager.getPayload().email)
    }

    private fun refreshMe() {
        UserRepository.scope.launch {
            emeraldApiClient.me().await().let {
                UserRepository.userDao.save(it)
            }
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
