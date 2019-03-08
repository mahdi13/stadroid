package com.stacrypt.stadroid.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.wallet.ui.balancedetail.BalanceDetailFragment

class BalanceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.balance_detail_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BalanceDetailFragment.newInstance(intent.getStringExtra(ARG_ASSET)))
                .commitNow()
        }
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
