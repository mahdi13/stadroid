package io.stacrypt.stadroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.Fragment
import io.stacrypt.stadroid.data.StemeraldDatabase
import io.stacrypt.stadroid.data.stemeraldDatabase
import io.stacrypt.stadroid.profile.ProfileFragment
import io.stacrypt.stadroid.wallet.WalletFragment
import androidx.room.Room
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.market.*
import io.stacrypt.stadroid.profile.LoginFragment
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity(),
    LoginFragment.OnLoginInteractionListener,
    ProfileFragment.OnProfileInteractionListener {

//    private lateinit var marketBackdropNavigationHandler: MarketBackdropNavigationHandler

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
//        marketBackdropNavigationHandler.collapse(true)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stemeraldDatabase = Room.databaseBuilder(
            applicationContext,
            StemeraldDatabase::class.java, "stemerald_db"
        ).build()

//        setSupportActionBar(backdrop_toggle)
//        supportActionBar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
//        }


//        marketBackdropNavigationHandler = MarketBackdropNavigationHandler(
//            this,
//            nested_content,
//            interpolator = AccelerateDecelerateInterpolator(),
//            toggleView = backdrop_toggle,
//            sheetList = backdrop_list,
//            loadingText = "Loading..."
//        )
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
}


