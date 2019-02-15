package com.stacrypt.stadroid

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.stacrypt.stadroid.data.StemeraldDatabase
import com.stacrypt.stadroid.data.stemeraldDatabase
import com.stacrypt.stadroid.profile.ProfileFragment
import com.stacrypt.stadroid.wallet.WalletFragment
import androidx.room.Room
import com.stacrypt.stadroid.data.Market
import com.stacrypt.stadroid.data.sessionManager
import com.stacrypt.stadroid.market.*
import com.stacrypt.stadroid.profile.LoginFragment
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(),
    LoginFragment.OnLoginInteractionListener,
    ProfileFragment.OnProfileInteractionListener {

    private lateinit var marketViewModel: MarketViewModel
    private lateinit var backdropNavigationHandler: BackdropNavigationHandler

    override fun onLoggedOut() {
        switchFragment(3, "3")
        toast("Logged out!")
    }

    override fun onLoggedIn() {
        switchFragment(2, "2")
    }

    override fun onRegistered() {}

    private val pages = ArrayList<Fragment>(3)


    private fun switchFragment(pos: Int, tag: String) {
        backdropNavigationHandler.collapse(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nested_content, pages[pos], tag)
            .commit()
    }

    private fun buildWalletFragment(): Fragment = WalletFragment()
    private fun buildMarketFragment(): Fragment = MarketFragment()
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
        backdrop_toggle.text = "Loading ..."

        backdropNavigationHandler = BackdropNavigationHandler(
            this@MainActivity,
            nested_content,
            AccelerateDecelerateInterpolator()
        )
        backdrop_toggle.setOnClickListener(backdropNavigationHandler)

        backdrop_list.adapter = MarketListRecyclerViewAdapter(listOf()) {
            marketViewModel.selectedMarketName.value = it
            backdropNavigationHandler.collapse()
        }
        backdrop_list.layoutManager = LinearLayoutManager(this)

        marketViewModel.market.observe(this, Observer<List<Market>> { markets ->
            if (markets == null) return@Observer

            val marketListAdapter = backdrop_list.adapter as MarketListRecyclerViewAdapter?
            marketListAdapter?.items = markets
            marketListAdapter?.notifyDataSetChanged()

            if (marketViewModel.selectedMarketName.value == null && markets.isNotEmpty())
                marketViewModel.selectedMarketName.value = markets[0].name
        })

        marketViewModel.selectedMarketName.observe(this, Observer<String> { marketName ->
            backdrop_toggle.text = marketName
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stemeraldDatabase = Room.databaseBuilder(
            applicationContext,
            StemeraldDatabase::class.java, "stemerald_db"
        ).build()

        marketViewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)

        setUpMarketBackdrop()
        buildFragmentsList()

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_wallet -> {
                    backdrop_toggle.visibility = View.GONE
                    switchFragment(0, "0")
                }
                R.id.navigation_market -> {
                    switchFragment(1, "1")
                    backdrop_toggle.visibility = View.VISIBLE
                }
                R.id.navigation_profile -> {
                    backdrop_toggle.visibility = View.GONE
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


