package io.stacrypt.stadroid.profile.banking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import io.stacrypt.stadroid.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

// TODO: Add rate limiter
object BankingRepository {
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Always online
     */
    fun getBankAccounts(): Listing<BankAccount> {
        val sourceFactory = BankAccountDataSourceFactory(fiatSymbol)

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
     * Always online
     */
    fun getBankCards(fiatSymbol: String? = null): Listing<BankCard> {
        val sourceFactory = BankCardDataSourceFactory(fiatSymbol)

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

    fun addBankAccount(
        iban: String,
        owner: String,
        fiatSymbol: String,
        bankAccountListing: Listing<BankAccount>?
    ): LiveData<BankAccount?> {
        val liveData = MutableLiveData<BankAccount?>()
        scope.launch {
            try {
                liveData.postValue(
                    stemeraldApiClient.addBankAccount(iban = iban, owner = owner, fiatSymbol = fiatSymbol).await()
                )
            } catch (e: Exception) {
                // TODO Show error
                liveData.postValue(null)
            } finally {
                // Refresh the list
                bankAccountListing?.refresh?.invoke()
            }
        }
        return liveData
    }

    fun addBankCard(
        pan: String,
        holder: String,
        fiatSymbol: String,
        bankCardListing: Listing<BankCard>?
    ): LiveData<BankCard?> {
        val liveData = MutableLiveData<BankCard?>()
        scope.launch {
            try {
                liveData.postValue(
                    stemeraldApiClient.addBankCard(pan = pan, holder = holder, fiatSymbol = fiatSymbol).await()
                )
            } catch (e: Exception) {
                // TODO Show error
                liveData.postValue(null)
            } finally {
                // Refresh the list
                bankCardListing?.refresh?.invoke()
            }
        }
        return liveData
    }
}
