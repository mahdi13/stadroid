package io.stacrypt.stadroid.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.PullerLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import io.stacrypt.stadroid.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

object NotificationRepository {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Always online
     */
    fun getNotifications(): Listing<Notification> {
        val sourceFactory = NotificationDataSourceFactory()

        val livePagedList = sourceFactory.toLiveData(pageSize = 20)

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
     * Always online
     */
    fun getNotificationsCount(): LiveData<NotificationCount> = PullerLiveData<NotificationCount>(scope, 1000) {
        stemeraldApiClient.countNotifications().await()
    }
}
