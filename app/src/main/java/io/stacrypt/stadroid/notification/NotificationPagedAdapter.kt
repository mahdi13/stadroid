package io.stacrypt.stadroid.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Notification
import io.stacrypt.stadroid.data.format
import io.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.android.synthetic.main.notification_list_content.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationPagedAdapter(activity: NotificationListActivity, twoPane: Boolean) :
    PagedListAdapter<Notification, NotificationPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Notification
            GlobalScope.launch { stemeraldApiClient.readNotification(notificationId = item.id).await() }

            if (twoPane) {
                val fragment = NotificationDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(NotificationDetailFragment.ARG_NOTIFICATION, Gson().toJson(item))
                    }
                }
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.notification_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(v.context, NotificationDetailActivity::class.java).apply {
                    putExtra(NotificationDetailFragment.ARG_NOTIFICATION, Gson().toJson(item))
                }
                v.context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindTo(getItem(position))


    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_list_content, parent, false)
    ) {
        val cardView: CardView = itemView.card.apply { setOnClickListener(onClickListener) }
        val titleView: TextView = itemView.title
        val descriptionView: TextView = itemView.description
        val dateView: TextView = itemView.date

        fun bindTo(notification: Notification?) {
            if (notification == null) return clear()

            cardView.tag = notification

            titleView.text = notification.title
            descriptionView.text = notification.description
            dateView.text = notification.createdAt.format()

            cardView.backgroundTintList = ContextCompat.getColorStateList(
                cardView.context,
                when {
                    notification.isRead -> R.color.colorPrimaryDark
                    else -> R.color.colorPrimary
                }
            )

        }

        private fun clear() {
            titleView.text = ""
            descriptionView.text = ""
            dateView.text = ""
        }

    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Notification>() {
            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem.id == newItem.id

        }

    }


}