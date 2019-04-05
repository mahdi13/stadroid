package io.stacrypt.stadroid.wallet

import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.wallet.data.WalletRepository

class WalletViewModel : ViewModel() {
    val balances = WalletRepository.getBalanceOverview()
}