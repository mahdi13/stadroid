package io.stacrypt.stadroid.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.wallet.deposit.DepositFragment
import io.stacrypt.stadroid.wallet.ui.balancedetail.BalanceDetailFragment
import io.stacrypt.stadroid.wallet.ui.balancedetail.BalanceDetailViewModel
import org.jetbrains.anko.support.v4.withArguments

class BalanceDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BalanceDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.balance_detail_activity)

        viewModel = ViewModelProviders.of(this).get(BalanceDetailViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BalanceDetailFragment())
                .commitNow()

            when (intent.getStringExtra(ARG_ACTION)) {
                ACTION_DEPOSIT -> showDeposit()
                ACTION_WITHDRAW -> showWithdraw()
            }
        }

        viewModel.assetName.value = intent.getStringExtra(ARG_ASSET)

    }

    fun showWithdraw() {
        val fragment = when (viewModel.currency.value?.type?.toLowerCase()) {
            "cryptocurrency" -> WithdrawFragment()
            "fiat" -> CashoutFragment()
            else -> null
        }

        if (fragment != null)
            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment.withArguments(ARG_ASSET to intent.getStringExtra(ARG_ASSET)))
                .commitNow()
    }

    fun showDeposit() {
        val fragment = when (viewModel.currency.value?.type?.toLowerCase()) {
            "cryptocurrency" -> DepositFragment()
            "fiat" -> CashinFragment()
            else -> null
        }

        if (fragment != null)
            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment.withArguments(ARG_ASSET to intent.getStringExtra(ARG_ASSET)))
                .commitNow()
    }

    companion object {
        val ARG_ASSET = "asset"
        val ARG_ACTION = "action"

        val ACTION_DEPOSIT = "deposit"
        val ACTION_WITHDRAW = "withdraw"
        val ACTION_HISTORY = "history"

        fun createIntent(context: Context, asset: String, action: String = "") =
            Intent(context, BalanceDetailActivity::class.java).putExtra(ARG_ASSET, asset).putExtra(ARG_ACTION, action)!!
    }

}
