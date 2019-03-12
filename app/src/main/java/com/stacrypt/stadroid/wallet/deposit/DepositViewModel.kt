package com.stacrypt.stadroid.wallet.deposit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import com.stacrypt.stadroid.wallet.data.WalletRepository


class DepositViewModel : ViewModel() {
    val assetName: MutableLiveData<String> = MutableLiveData()

    val asset = switchMap(assetName) { WalletRepository.getAsset(it) }

    val depositInfo = switchMap(assetName) {
        WalletRepository.getDepositInfo(it)
    }

}
