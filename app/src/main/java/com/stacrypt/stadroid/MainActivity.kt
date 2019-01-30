package com.stacrypt.stadroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.AccelerateDecelerateInterpolator
import com.stacrypt.stadroid.market.BackdropNavigationHandler
import org.jetbrains.anko.toast
import androidx.fragment.app.Fragment
import com.stacrypt.stadroid.market.MarketActivityFragment


class MainActivity : AppCompatActivity() {

    private val fragments = ArrayList<Fragment>(3)


    private fun switchFragment(pos: Int, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragments[pos], tag)
            .commit()
    }

    private fun buildFragment(title: String): Fragment {
        val fragment = MarketActivityFragment()
        val bundle = Bundle()
//        bundle.putString(Fragment.ARG_TITLE, title)
//        fragment.setArguments(bundle)
        return fragment
    }


    private fun buildFragmentsList() {
        val callsFragment = buildFragment("Calls")
        val recentsFragment = buildFragment("Recents")
        val tripsFragment = buildFragment("Trips")

        fragments.add(callsFragment)
        fragments.add(recentsFragment)
        fragments.add(tripsFragment)
    }

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
        buildFragmentsList()

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_wallet -> {
                    switchFragment(0, "0")
                }
                R.id.navigation_market -> {
                    switchFragment(1, "1")
                }
                R.id.navigation_profile -> {
                    switchFragment(2, "2")
                }
            }
            true
        }

        // Set the Market Fragment to be displayed by default.
        navigation.selectedItemId = R.id.navigation_market

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


