package com.stacrypt.stadroid.profile.banking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.BankAccount
import com.stacrypt.stadroid.data.BankCard
import com.stacrypt.stadroid.data.Listing

class BankCardsViewModel : ViewModel() {
    private val bankCardListing: MutableLiveData<Listing<BankCard>> by lazy {
        MutableLiveData<Listing<BankCard>>().apply { postValue(BankingRepository.getBankCards()) }
    }
    val bankCards = switchMap(bankCardListing) { it.pagedList }
    val networkState = switchMap(bankCardListing) { it.networkState }
    val refreshState = switchMap(bankCardListing) { it.refreshState }
}
