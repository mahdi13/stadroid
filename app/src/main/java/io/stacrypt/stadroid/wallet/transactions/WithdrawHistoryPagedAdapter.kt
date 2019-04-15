package io.stacrypt.stadroid.wallet.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Withdraw
import io.stacrypt.stadroid.profile.banking.digestAddress
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.row_deposit_history.view.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

class WithdrawHistoryPagedAdapter(val cryptocurrencySymbol: String) :
    PagedListAdapter<Withdraw, WithdrawHistoryPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    private val sdfDate = SimpleDateFormat("dd-MM", Locale.ENGLISH)
    private val sdfTime = SimpleDateFormat("hh:mm", Locale.ENGLISH)

    var onItemClickListener: ((Withdraw) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))

    inner class ViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_deposit_history, parent, false)
    ) {
        val cardView: CardView = itemView.card
        val statusView: TextView = itemView.status
        val amountView: TextView = itemView.net_amount
        val feeView: TextView = itemView.fee
        val progressView: BaseRoundCornerProgressBar = itemView.progress
        val createdView: TextView = itemView.created
        val modifiedView: TextView = itemView.modified
        val addressView: TextView = itemView.address

        init {
            progressView.visibility = View.GONE
        }

        fun bindTo(withdraw: Withdraw?) {
            cardView.setOnClickListener { v ->
                withdraw?.let { onItemClickListener?.invoke(it) }
            }

            if (withdraw == null) return clear()

            amountView.text = (withdraw.netAmount ?: BigDecimal.ZERO).format10Digit() + " " + cryptocurrencySymbol
            feeView.text = "Fee: " + (withdraw.grossAmount
                ?.minus(withdraw.netAmount ?: withdraw.grossAmount!!)?.abs()
                ?: BigDecimal.ZERO).format10Digit() + " " + cryptocurrencySymbol

            createdView.text = withdraw.issuedAt?.let { sdfDate.format(it) } ?: ""
            modifiedView.text =
                (withdraw.paidAt ?: withdraw.issuedAt)?.let { "Last Update at " + sdfTime.format(it) } ?: ""

            addressView.text = withdraw.toAddress?.digestAddress() ?: ""

            when {
                !withdraw.error.isNullOrEmpty() -> {
                    statusView.text = "Error"
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_red))
                }
                withdraw.confirmationsLeft == 0 -> {
                    statusView.text = "Received"
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_green))
                }
                !withdraw.txid.isNullOrEmpty() -> {
                    statusView.text = "Confirming..."
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_green))
                }
                withdraw.paidAt != null -> {
                    statusView.text = "Paid..."
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_green))
                }
                else -> {
                    statusView.text = "Scheduled..."
                    statusView.setTextColor(parent.context.resources.getColor(R.color.colorSecondary))
                }
            }
        }

        private fun clear() {
            cardView.setOnClickListener { }
            statusView.text = ""
            amountView.text = ""
            feeView.text = ""
            modifiedView.text = ""
            createdView.text = ""
            addressView.text = ""
        }
    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Withdraw>() {
            override fun areContentsTheSame(oldItem: Withdraw, newItem: Withdraw): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Withdraw, newItem: Withdraw): Boolean =
                oldItem.id == newItem.id
        }
    }
}
