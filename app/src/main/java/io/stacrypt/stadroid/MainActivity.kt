package io.stacrypt.stadroid

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.data.StemeraldDatabase
import io.stacrypt.stadroid.data.stemeraldDatabase
import io.stacrypt.stadroid.profile.ProfileFragment
import io.stacrypt.stadroid.wallet.WalletFragment
import androidx.room.Room
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.market.*
import io.stacrypt.stadroid.notification.NotificationListActivity
import io.stacrypt.stadroid.profile.LoginFragment
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(),
    LoginFragment.OnLoginInteractionListener,
    ProfileFragment.OnProfileInteractionListener {

    private lateinit var viewModel: MainViewModel
//    private lateinit var marketBackdropNavigationHandler: MarketBackdropNavigationHandler

    override fun onLoggedOut() {
        switchFragment(3)
        toast("Logged out!")
    }

    override fun onLoggedIn() {
        switchFragment(2)
    }

    override fun onRegistered() {}

    private val pages = ArrayList<Pair<Fragment, String>>(3)

    private fun switchFragment(pos: Int) {
//        marketBackdropNavigationHandler.collapse(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nested_content, pages[pos].first, pages[pos].second)
            .commit()
        toolbar_title.text = pages[pos].second
    }

    private fun buildWalletFragment(): Fragment = WalletFragment()
    private fun buildMarketFragment(): Fragment = MarketVitrineFragment()
    private fun buildProfileFragment(): Fragment = ProfileFragment()
    private fun buildLoginFragment(): Fragment = LoginFragment()

    private fun buildFragmentsList() {
        pages.add(buildWalletFragment() to "My Wallet")
        pages.add(buildMarketFragment() to "Trading")
        pages.add(buildProfileFragment() to "My Profile")
        pages.add(buildLoginFragment() to "Login or Register")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stemeraldDatabase = Room.databaseBuilder(
            applicationContext,
            StemeraldDatabase::class.java, "stemerald_db"
        )
            .fallbackToDestructiveMigration() // FIXME: Not a great approach in production
            .build()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.notificationsCount.observe(this, Observer {
            val unreadNotifications = it?.unread ?: 0

            notification_badge.visibility = View.VISIBLE
            if (unreadNotifications == 0) notification_badge.visibility = View.GONE
            else notification_badge.text = unreadNotifications.toString()
        })

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
                    if (sessionManager.isLoggedIn()) switchFragment(0)
                    else switchFragment(3)
                }
                R.id.navigation_market -> {
                    switchFragment(1)
                }
                R.id.navigation_profile -> {
                    if (sessionManager.isLoggedIn()) switchFragment(2)
                    else switchFragment(3)
                }
            }
            true
        }

        // Set the Market Fragment to be displayed by default.
        navigation.selectedItemId = R.id.navigation_market

        notifications.setOnClickListener { startActivity<NotificationListActivity>() }
    }

    override fun onResume() {
        super.onResume()
        // TODO: Refresh notificationsCount
    }
}

