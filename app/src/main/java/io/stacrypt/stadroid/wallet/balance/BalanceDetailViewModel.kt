package io.stacrypt.stadroid.wallet.balance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import io.stacrypt.stadroid.wallet.data.WalletRepository

class BalanceDetailViewModel : ViewModel() {
    val assetName: MutableLiveData<String> = MutableLiveData()

    val asset = switchMap(assetName) { WalletRepository.getAsset(it) }
    val balance = switchMap(assetName) { WalletRepository.getBalanceOverview(it) }
    val currency = switchMap(assetName) { WalletRepository.getCurrency(it) }

    private val balanceHistoryListing = map(assetName) { WalletRepository.getBalanceHistory(it) }
    val balanceHistory = switchMap(balanceHistoryListing) { it.pagedList }
    val networkState = switchMap(balanceHistoryListing) { it.networkState }
    val refreshState = switchMap(balanceHistoryListing) { it.refreshState }

//    val depositInfo = object : MutableLiveData<DepositInfo?>() {
//
//        init {
//            switchMap(assetName) {
//                WalletRepository.getDepositInfo(it)
//            }.observeForever {
//                postValue(it)
//            }
//        }
//    }
//    val depositInfo by lazy {
//        (switchMap(assetName) {
//            WalletRepository.getDepositInfo(it)
//        } as MediatorLiveData<DepositInfo>)
//    }

//    fun renewDepositInfo() {
//        WalletRepository.renewDepositInfo(assetName.value!!, depositInfo)
//        // FIXME: ReObserve the asset_name after renew
//    }

    val paymentMethods by lazy { switchMap(assetName) { WalletRepository.getPaymentMethods(it) } }
}
