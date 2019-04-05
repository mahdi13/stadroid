package io.stacrypt.stadroid.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Notification
import io.stacrypt.stadroid.wallet.balance.BalanceDetailViewModel

import kotlinx.android.synthetic.main.activity_notification_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [NotificationDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class NotificationListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private lateinit var viewModel: NotificationViewModel
    private lateinit var adapter: NotificationPagedAdapter
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        // Show the Up button in the action bar.
        back.setOnClickListener { finish() }

        if (notification_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)

        adapter = NotificationPagedAdapter(this, twoPane)
        notification_list.adapter = adapter

        viewModel.notifications.observe(this, Observer<PagedList<Notification>> {
            adapter.submitList(it)
        })


    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}


