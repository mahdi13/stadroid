package io.stacrypt.stadroid.wallet

import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.wallet.data.WalletRepository

class WalletViewModel : ViewModel() {

    // val rateLimiter: RateLimiter
    val balances = WalletRepository.getBalanceOverview()/*.apply {
        GlobalScope.launch {
            while (true) {
                if(this@apply.hasActiveObservers())WalletRepository.refreshBalanceOverview()
                delay(1000)
            }
        }
    }*/

    // TODO: We need this to be updated:
    // 1. Whenever

    // fun refresh(force: Boolean) {
    //     if (!force) {
    //         // TODO: Check rate limiter
    //     }
    //
    //
    // }
}