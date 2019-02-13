package com.stacrypt.stadroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.AccelerateDecelerateInterpolator
import com.stacrypt.stadroid.market.BackdropNavigationHandler
import androidx.fragment.app.Fragment
import com.stacrypt.stadroid.data.StemeraldDatabase
import com.stacrypt.stadroid.data.stemeraldDatabase
import com.stacrypt.stadroid.market.MarketActivityFragment
import com.stacrypt.stadroid.profile.ProfileFragment
import com.stacrypt.stadroid.wallet.WalletFragment
import androidx.room.Room
import com.stacrypt.stadroid.data.sessionManager
import com.stacrypt.stadroid.profile.LoginFragment


class MainActivity : AppCompatActivity(),
    LoginFragment.OnLoginInteractionListener,
    ProfileFragment.OnProfileInteractionListener {
    override fun onLoggedOut() {
        switchFragment(3, "3")
    }

    override fun onLoggedIn() {
        switchFragment(2, "2")
    }

    override fun onRegistered() {}

    private val pages = ArrayList<Fragment>(3)


    private fun switchFragment(pos: Int, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nested_content, pages[pos], tag)
            .commit()
    }

    private fun buildWalletFragment(): Fragment = WalletFragment()
    private fun buildMarketFragment(): Fragment = MarketActivityFragment()
    private fun buildProfileFragment(): Fragment = ProfileFragment()
    private fun buildLoginFragment(): Fragment = LoginFragment()


    private fun buildFragmentsList() {
        pages.add(buildWalletFragment())
        pages.add(buildMarketFragment())
        pages.add(buildProfileFragment())
        pages.add(buildLoginFragment())
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

//        GlobalScope.launch(Dispatchers.Main) {
//            try {
////                val markets = stemeraldApiClient.marketList().await()
//                val markets = arrayListOf(
//                    Market("BTC/ETH", 0L, 0L, 0L, 0F, 0L, 0F),
//                    Market("BCH/ETH", 0L, 0L, 0L, 0F, 0L, 0F),
//                    Market("BTC/BCH", 0L, 0L, 0L, 0F, 0L, 0F)
//                )
//                backdrop_list.removeAllViews()
//                markets.forEach {
//                    val listItem = layoutInflater.inflate(R.layout.backdrop_market_row, backdrop_list, true)
////                    listItem.text = it.name
//                    backdrop_list.addView(listItem)
//                }
//            } catch (e: Exception) {
//                toast(e.toString())
//            }
//        }

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

        stemeraldDatabase = Room.databaseBuilder(
            applicationContext,
            StemeraldDatabase::class.java, "stemerald_db"
        ).build()


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
                    if (sessionManager.isLoggedIn()) switchFragment(2, "2")
                    else switchFragment(3, "3")
                }
            }
            true
        }

        // Set the Market Fragment to be displayed by default.
        navigation.selectedItemId = R.id.navigation_market

    }

    override fun onStart() {
        super.onStart()
//        stexchangeApiClient.start()

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
//        stexchangeApiClient.shutdown()
    }
}


