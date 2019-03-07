package com.stacrypt.stadroid.wallet.ui.balancedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.BalanceHistory
import com.stacrypt.stadroid.data.BalanceOverview
import kotlinx.android.synthetic.main.row_balance_detail_history.view.*

class BalanceDetailPagedAdapter(var balanceOverview: BalanceOverview?) :
    PagedListAdapter<BalanceHistory, RecyclerView.ViewHolder>(ITEM_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.row_balance_detail_header -> HistoryViewHolder(parent)
            else -> HeaderViewHolder(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (getItemViewType(position)) {
            R.layout.row_balance_detail_header -> (holder as HeaderViewHolder).bindTo(balanceOverview)
            else -> (holder as HistoryViewHolder).bindTo(getItem(position - 1))
        }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.row_balance_detail_header
        else -> R.layout.row_balance_detail_history
    }

    inner class HeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_balance_detail_header, parent, false)
    ) {
        val titleView: TextView = itemView.title
        val amountView: TextView = itemView.amount
        val valueView: TextView = itemView.value

        fun bindTo(item: BalanceOverview?) {
            if (item == null) return
            titleView.text = "Your ${item.assetName} Balance:"
            amountView.text = item.available.toString()
            valueView.text = "1234 $" // FIXME

        }

    }

    inner class HistoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_balance_detail_history, parent, false)
    ) {
        val titleView: TextView = itemView.title
        val dateView: TextView = itemView.date
        val amountView: TextView = itemView.amount
        val valueView: TextView = itemView.value

        fun bindTo(item: BalanceHistory?) {
            if (item == null) return clear()

            titleView.text = item.business
            dateView.text = item.time.toString()
            amountView.text = item.change
            valueView.text = "1234 $" // FIXME

        }

        private fun clear() {
            titleView.text = ""
            dateView.text = ""
            amountView.text = ""
            valueView.text = ""
        }

    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<BalanceHistory>() {
            override fun areContentsTheSame(oldItem: BalanceHistory, newItem: BalanceHistory): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: BalanceHistory, newItem: BalanceHistory): Boolean =
                oldItem.time == newItem.time

        }

    }


}