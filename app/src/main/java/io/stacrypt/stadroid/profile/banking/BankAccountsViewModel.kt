package io.stacrypt.stadroid.profile.banking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.data.BankAccount
import io.stacrypt.stadroid.data.Listing

class BankAccountsViewModel : ViewModel() {
    private val bankAccountListing: MutableLiveData<Listing<BankAccount>> by lazy {
        MutableLiveData<Listing<BankAccount>>().apply { postValue(BankingRepository.getBankAccounts()) }
    }
    val bankAccounts = switchMap(bankAccountListing) { it.pagedList }
    val networkState = switchMap(bankAccountListing) { it.networkState }
    val refreshState = switchMap(bankAccountListing) { it.refreshState }
}
