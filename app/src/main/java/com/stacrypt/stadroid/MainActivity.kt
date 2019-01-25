package com.stacrypt.stadroid

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import com.stacrypt.stadroid.market.BackdropNavigationHandler
import org.jetbrains.anko.textResource
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

//    private fun setUpToolbar() {
//        val toolbar = app_bar
//        setSupportActionBar(toolbar)
//
//        // Set cut corner background for API 23+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            nested_content.background = getDrawable(R.drawable.market_backdrop_background_shape)
//        }
//
//
//        toolbar.setNavigationOnClickListener(
//            BackdropNavigationHandler(
//                this@MainActivity,
//                nested_content,
//                AccelerateDecelerateInterpolator(),
//                this@MainActivity.resources.getDrawable(R.drawable.ic_home_white_24dp), // Menu open icon
//                this@MainActivity.resources.getDrawable(R.drawable.ic_dashboard_white_24dp)
//              )
//        ) // Menu close icon
//    }g

    private fun setUpMarketBackdrop() {
        backdrop_edge.text = "Loading ..."

        GlobalScope.launch(Dispatchers.Main) {
            try {
//                val markets = stemeraldApiClient.marketList().await()
                val markets = arrayListOf(
                    Market("BTC/ETH", 0L, 0L, 0L, 0F, 0L, 0F),
                    Market("BCH/ETH", 0L, 0L, 0L, 0F, 0L, 0F),
                    Market("BTC/BCH", 0L, 0L, 0L, 0F, 0L, 0F)
                )
                backdrop_list.removeAllViews()
                markets.forEach {
                    val listItem = layoutInflater.inflate(R.layout.backdrop_market_row, backdrop_list, true)
//                    listItem.text = it.name
                    backdrop_list.addView(listItem)
                }
            } catch (e: Exception) {
                toast(e.toString())
            }
        }

        backdrop_edge_container.setOnClickListener(
            BackdropNavigationHandler(
                this@MainActivity,
                nested_content,
                AccelerateDecelerateInterpolator(),
                resources.getDrawable(android.R.drawable.arrow_up_float),
                resources.getDrawable(android.R.drawable.arrow_down_float)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpMarketBackdrop()

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
//                    message.setText(R.string.title_wallet)
                }
                R.id.navigation_dashboard -> {
//                    message.setText(R.string.title_market)
                }
                R.id.navigation_notifications -> {
//                    message.setText(R.string.title_profile)
                }
            }
            true
        }


    }

    override fun onStart() {
        super.onStart()
        stexchangeApiClient.start()

        GlobalScope.launch(Dispatchers.Main) {
            //            while (true) {
//                try {
//                    toast(stexchangeApiClient.ping())
//                } catch (e: Exception) {
//                    toast(e.toString())
//                    delay(2000)
//                }
//            }
        }

    }

    override fun onStop() {
        super.onStop()
        stexchangeApiClient.shutdown()
    }
}


