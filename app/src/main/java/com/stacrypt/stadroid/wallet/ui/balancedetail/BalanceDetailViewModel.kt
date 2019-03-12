package com.stacrypt.stadroid.wallet.ui.balancedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import com.stacrypt.stadroid.wallet.data.WalletRepository


class BalanceDetailViewModel : ViewModel() {
    val assetName: MutableLiveData<String> = MutableLiveData()

    val asset = switchMap(assetName) { WalletRepository.getAsset(it) }
    val balance = switchMap(assetName) { WalletRepository.getBalanceOverview(it) }

    private val balanceHistoryListing = map(assetName) {
        WalletRepository.getBalanceHistory(it)
    }
    val balanceHistory = switchMap(balanceHistoryListing) { it.pagedList }
    val networkState = switchMap(balanceHistoryListing) { it.networkState }
    val refreshState = switchMap(balanceHistoryListing) { it.refreshState }

}
