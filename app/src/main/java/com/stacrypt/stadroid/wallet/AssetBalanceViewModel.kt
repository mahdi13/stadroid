package com.stacrypt.stadroid.wallet

import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.WalletRepository

class AssetBalanceViewModel : ViewModel() {
    val balances = WalletRepository.getBalances()


}