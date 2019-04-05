package io.stacrypt.stadroid.market

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.stacrypt.stadroid.R
import org.jetbrains.anko.support.v4.withArguments
import org.jetbrains.anko.toast

class MarketActivity : AppCompatActivity() {

    companion object {
        const val ARG_MARKET = "market"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)

        val market = intent.getStringExtra(ARG_MARKET)

        if (market == null) {
            toast(R.string.problem_occurred_toast)
            finish()
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, MarketFragment().withArguments(ARG_MARKET to market), market)
            .commitNow()
    }
}
