package io.stacrypt.stadroid.wallet.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.DepositDetail
import io.stacrypt.stadroid.profile.banking.digestAddress
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.row_deposit_history.view.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

class DepositHistoryPagedAdapter(val cryptocurrencySymbol: String) :
    PagedListAdapter<DepositDetail, DepositHistoryPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((DepositDetail) -> Unit)? = null

    private val sdfDate = SimpleDateFormat("dd-MM", Locale.ENGLISH)
    private val sdfTime = SimpleDateFormat("hh:mm", Locale.ENGLISH)

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

        fun bindTo(depositDetail: DepositDetail?) {
            cardView.setOnClickListener { v ->
                depositDetail?.let { onItemClickListener?.invoke(it) }
            }


            if (depositDetail == null) return clear()

            progressView.max = depositDetail.confirmationsRequires?.toFloat() ?: 0f
            progressView.progress = depositDetail.confirmationsRequires?.minus(
                depositDetail.confirmationsLeft ?: depositDetail.confirmationsRequires!!
            )?.toFloat() ?: 0f

            amountView.text = (depositDetail.netAmount ?: BigDecimal.ZERO).format10Digit() + " " + cryptocurrencySymbol
            feeView.text = "Fee: " + (depositDetail.grossAmount
                ?.minus(depositDetail.netAmount ?: depositDetail.grossAmount!!)?.abs()
                ?: BigDecimal.ZERO).format10Digit() + " " + cryptocurrencySymbol

            createdView.text = depositDetail.invoice.creation?.let { sdfDate.format(it) } ?: ""
            modifiedView.text =
                (depositDetail.invoice.expiration)?.let { "Expires on: " + sdfTime.format(it) } ?: ""

            addressView.text = depositDetail.toAddress.address.digestAddress()

            when (depositDetail.status.toLowerCase()) {
                "failed" -> {
                    statusView.text = "Failed"
                    progressView.progressColor = (parent.context.resources.getColor(R.color.real_red))
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_red))
                }
                "unacceptable" -> {
                    statusView.text = "Unacceptable"
                    progressView.progressColor = (parent.context.resources.getColor(R.color.real_red))
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_red))
                }
                "accepted" -> {
                    statusView.text = "Accepted"
                    progressView.progressColor = (parent.context.resources.getColor(R.color.real_green))
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_green))
                }
                "waiting_to_be_confirmed" -> {
                    statusView.text = "Confirming..."
                    progressView.progressColor = (parent.context.resources.getColor(R.color.colorSecondary))
                    statusView.setTextColor(parent.context.resources.getColor(R.color.colorSecondary))
                }
                "orphan" -> {
                    statusView.text = "Mining..."
                    progressView.progressColor = (parent.context.resources.getColor(R.color.yellow))
                    statusView.setTextColor(parent.context.resources.getColor(R.color.yellow))
                }
                else -> {
                    statusView.text = "Unknown"
                    progressView.progressColor = (parent.context.resources.getColor(R.color.colorGrayText))
                    statusView.setTextColor(parent.context.resources.getColor(R.color.colorGrayText))
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
            progressView.progress = 0F
        }
    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<DepositDetail>() {
            override fun areContentsTheSame(oldItem: DepositDetail, newItem: DepositDetail): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: DepositDetail, newItem: DepositDetail): Boolean =
                oldItem.id == newItem.id
        }
    }
}
