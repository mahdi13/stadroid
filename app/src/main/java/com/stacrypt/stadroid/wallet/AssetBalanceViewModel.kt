package com.stacrypt.stadroid.wallet

import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.wallet.data.WalletRepository

class AssetBalanceViewModel : ViewModel() {
    val balances = WalletRepository.getBalanceOverview()


}