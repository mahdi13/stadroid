package androidx.lifecycle

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class PullingStatus { FIRST_LOADING, LOADING, LIVE, ERROR }

class PullerLiveData<T>(scope: CoroutineScope, interval: Long, executor: suspend () -> T?) : MutableLiveData<T>() {

    private val job: Job = scope.launch {
        do {
            if (hasActiveObservers()) {
                if (pullingStatus.value != PullingStatus.FIRST_LOADING) pullingStatus.postValue(PullingStatus.LOADING)
                try {
                    postValue(executor.invoke())
                    pullingStatus.postValue(PullingStatus.LIVE)
                } catch (e: Exception) {
                    pullingStatus.postValue(PullingStatus.ERROR)
                    e.printStackTrace()
                }
                Log.e("salam updating", this@PullerLiveData.toString())
            }
            delay(interval)
            Log.e("salam alive", this@PullerLiveData.toString())
        } while (true)
    }
    val pullingStatus = MutableLiveData<PullingStatus>().apply { postValue(PullingStatus.FIRST_LOADING) }

    fun destroy() {
        runBlocking {
            if (job.isActive) job.cancelAndJoin()
        }
    }
}