package io.stacrypt.stadroid.wallet.balance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.wallet.CashinFragment
import io.stacrypt.stadroid.wallet.CashoutFragment
import io.stacrypt.stadroid.wallet.WithdrawFragment
import io.stacrypt.stadroid.wallet.deposit.DepositFragment
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
                .commit()

            when (intent.getStringExtra(ARG_ACTION)) {
                ACTION_DEPOSIT -> showDeposit(intent.getStringExtra(ARG_ASSET))
                ACTION_WITHDRAW -> showWithdraw(intent.getStringExtra(ARG_ASSET))
            }
        }

        // FIXME: Empty observer to LiveData switchMaps work fine
        viewModel.currency.observe(this, Observer { })
        viewModel.asset.observe(this, Observer { })
        viewModel.balance.observe(this, Observer { })
        viewModel.currency.observe(this, Observer { })
        viewModel.currency.observe(this, Observer { })

        viewModel.assetName.value = intent.getStringExtra(ARG_ASSET)

    }

    fun showWithdraw(symbol: String) {
//        val fragment = when (viewModel.currency.value?.type?.toLowerCase()) {
//            "cryptocurrency" -> WithdrawFragment()
//            "fiat" -> CashoutFragment()
//            else -> null
//        }

        val fragment = when (symbol) {
            "BTC" -> WithdrawFragment()
            "TBTC" -> WithdrawFragment()
            "IRR" -> CashoutFragment()
            "TIRR" -> CashoutFragment()
            else -> null
        }


        if (fragment != null)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(
                    R.id.container, fragment.withArguments(
                        ARG_ASSET to intent.getStringExtra(
                            ARG_ASSET
                        )
                    )
                )
                .addToBackStack("$symbol-withdraw")
                .commit()

    }

    fun showDeposit(symbol: String) {
//        val fragment = when (viewModel.currency.value?.type?.toLowerCase()) {
//            "cryptocurrency" -> DepositFragment()
//            "fiat" -> CashinFragment()
//            else -> null
//        }
        val fragment = when (symbol) {
            "BTC" -> DepositFragment()
            "TBTC" -> DepositFragment()
            "IRR" -> CashinFragment()
            "TIRR" -> CashinFragment()
            else -> null
        }

        if (fragment != null)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(
                    R.id.container, fragment.withArguments(
                        ARG_ASSET to intent.getStringExtra(
                            ARG_ASSET
                        )
                    )
                )
                .addToBackStack("$symbol-deposit")
                .commit()
    }

    override fun onBackPressed() =
        if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()
        else super.onBackPressed()


    companion object {
        val ARG_ASSET = "asset"
        val ARG_ACTION = "action"

        val ACTION_DEPOSIT = "deposit"
        val ACTION_WITHDRAW = "withdraw"
        val ACTION_HISTORY = "history"

        fun createIntent(context: Context, asset: String, action: String = "") =
            Intent(context, BalanceDetailActivity::class.java).putExtra(ARG_ASSET, asset).putExtra(
                ARG_ACTION, action
            )!!
    }

}