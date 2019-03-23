package io.stacrypt.stadroid.wallet.deposit

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import io.stacrypt.stadroid.data.Currency
import io.stacrypt.stadroid.data.DepositInfo
import io.stacrypt.stadroid.wallet.data.WalletRepository
import java.lang.Exception

class DepositViewModel : ViewModel() {

    lateinit var cryptocurrencySymbol: String
    lateinit var currency: LiveData<Currency>
    lateinit var depositInfo: LiveData<DepositInfo?>

    fun init(cryptocurrencySymbol: String) {

        this.cryptocurrencySymbol = cryptocurrencySymbol

        currency = WalletRepository.getCurrency(cryptocurrencySymbol)
        depositInfo = WalletRepository.getDepositInfo(cryptocurrencySymbol)
    }

    fun renewDepositInfo(addressNotUsedHandler: () -> Unit) {
        depositInfo = WalletRepository.renewDepositInfo(cryptocurrencySymbol, addressNotUsedHandler)
    }

//    val asset = Transformations.switchMap(assetName) { WalletRepository.getAsset(it) }
//    val balance = Transformations.switchMap(assetName) { WalletRepository.getBalanceOverview(it) }
//
//    private val balanceHistoryListing = Transformations.map(assetName) {
//        WalletRepository.getBalanceHistory(it)
//    }
//    val balanceHistory = Transformations.switchMap(balanceHistoryListing) { it.pagedList }
//    val networkState = Transformations.switchMap(balanceHistoryListing) { it.networkState }
//    val refreshState = Transformations.switchMap(balanceHistoryListing) { it.refreshState }
//
//    val depositInfo = object : MutableLiveData<DepositInfo?>() {
//
//        init {
//            Transformations.switchMap(assetName) {
//                WalletRepository.getDepositInfo(it)
//            }.observeForever {
//                postValue(it)
//            }
//        }
//    }
////    val depositInfo by lazy {
////        (switchMap(assetName) {
////            WalletRepository.getDepositInfo(it)
////        } as MediatorLiveData<DepositInfo>)
////    }
//
//    fun renewDepositInfo() {
//        WalletRepository.renewDepositInfo(assetName.value!!, depositInfo)
//        // FIXME: ReObserve the asset_name after renew
//    }

//    val paymentGateways by lazy { Transformations.switchMap(assetName) { WalletRepository.getPaymentGateways(it) } }

}