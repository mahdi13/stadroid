package io.stacrypt.stadroid.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Notification
import kotlinx.android.synthetic.main.notification_detail.view.*

/**
 * A fragment representing a single Notification detail screen.
 * This fragment is either contained in a [NotificationListActivity]
 * in two-pane mode (on tablets) or a [NotificationDetailActivity]
 * on handsets.
 */
class NotificationDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var notification: Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_NOTIFICATION)) {
                notification = Gson().fromJson(it.getString(ARG_NOTIFICATION), Notification::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.notification_detail, container, false)

        rootView.title.text = notification?.title
        rootView.description.text = notification?.description

        return rootView
    }

    companion object {
        const val ARG_NOTIFICATION = "notification"
    }
}
