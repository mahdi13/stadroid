package io.stacrypt.stadroid.wallet.ui.balancedetail

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BalanceHistory
import io.stacrypt.stadroid.data.BalanceOverview
import io.stacrypt.stadroid.data.format
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.iconResource
import kotlinx.android.synthetic.main.row_balance_detail_header.view.*
import kotlinx.android.synthetic.main.row_balance_detail_history.view.*
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.textResource

class BalanceDetailPagedAdapter(var balanceOverview: BalanceOverview?) :
    PagedListAdapter<BalanceHistory, RecyclerView.ViewHolder>(ITEM_COMPARATOR) {

    var onDepositClicked: ((String) -> Unit)? = null
    var onWithdrawClicked: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.row_balance_detail_header -> HeaderViewHolder(parent)
            else -> HistoryViewHolder(parent)
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
        val subjectView: TextView = itemView.subject
        val iconView: ImageView = itemView.header_icon
        val titleView: TextView = itemView.header_title
        val amountView: TextView = itemView.header_amount
        val valueView: TextView = itemView.header_value
        val depositView: TextView = itemView.deposit
        val withdrawView: TextView = itemView.withdraw

        init {
            depositView.setOnClickListener { onDepositClicked?.invoke(itemView.tag.toString()) }
            withdrawView.setOnClickListener { onWithdrawClicked?.invoke(itemView.tag.toString()) }
        }

        fun bindTo(item: BalanceOverview?) {
            if (item == null) return
            subjectView.text = "${item.currency.name}'s Wallet"
            iconView.setImageResource(item.currency.iconResource()!!)
            titleView.text = "Your ${item.assetName} Balance:"
            amountView.text = item.available.format10Digit()
            valueView.text = item.assetName // FIXME It should be the value
            itemView.tag = item.assetName
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
        val iconView: ImageView = itemView.icon

        fun bindTo(item: BalanceHistory?) {
            if (item == null) return clear()

            titleView.text = item.business
            dateView.text = item.time?.format().toString()
            amountView.text = item.change.format10Digit()
            valueView.text = item.balance.format10Digit() // FIXME It should be the value

            // TODO: Enhance these color and resource loadings
            if (item.business?.toLowerCase().equals("deposit")) {
                titleView.textResource = R.string.deposit
                titleView.textColorResource = R.color.real_green
                amountView.textColorResource = R.color.real_green
                iconView.setImageResource(R.drawable.ic_add_black_24dp)
                ImageViewCompat.setImageTintList(
                    iconView,
                    ColorStateList.valueOf(itemView.resources.getColor(R.color.real_green))
                )
            } else if (item.business?.toLowerCase().equals("withdraw")) {
                titleView.textResource = R.string.withdraw
                titleView.textColorResource = R.color.real_red
                amountView.textColorResource = R.color.real_red
                iconView.setImageResource(R.drawable.ic_send_black_24dp)
                ImageViewCompat.setImageTintList(
                    iconView,
                    ColorStateList.valueOf(itemView.resources.getColor(R.color.real_red))
                )
            } else if (item.business?.toLowerCase().equals("trade")) {
                titleView.textResource = R.string.trade
                titleView.textColorResource = R.color.colorSecondary
                amountView.textColorResource = R.color.colorSecondary
                iconView.setImageResource(R.drawable.ic_swap_horiz_black_24dp)
                ImageViewCompat.setImageTintList(
                    iconView,
                    ColorStateList.valueOf(itemView.resources.getColor(R.color.colorSecondary))
                )
            } else {
                titleView.textColorResource = R.color.white
                amountView.textColorResource = R.color.white
                iconView.setImageResource(R.drawable.ic_done_white_18dp)
                ImageViewCompat.setImageTintList(
                    iconView,
                    ColorStateList.valueOf(itemView.resources.getColor(R.color.white))
                )
            }

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