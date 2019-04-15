package io.stacrypt.stadroid.wallet.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankingTransaction
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.ibanSecurityMask
import io.stacrypt.stadroid.ui.panSecurityMask
import kotlinx.android.synthetic.main.row_banking_transaction_history.view.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

class BankingTransactionHistoryPagedAdapter(val fiatSymbol: String) :
    PagedListAdapter<BankingTransaction, BankingTransactionHistoryPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((BankingTransaction) -> Unit)? = null

    private val sdfDate = SimpleDateFormat("dd-MM", Locale.ENGLISH)
    private val sdfTime = SimpleDateFormat("hh:mm", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))

    inner class ViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_banking_transaction_history, parent, false)
    ) {
        val cardView: CardView = itemView.card
        val statusView: TextView = itemView.status
        val amountView: TextView = itemView.net_amount
        val feeView: TextView = itemView.fee
        val createdView: TextView = itemView.created
        val modifiedView: TextView = itemView.modified
        val addressView: TextView = itemView.address

        fun bindTo(bankingTransaction: BankingTransaction?) {
            cardView.setOnClickListener { v ->
                bankingTransaction?.let { onItemClickListener?.invoke(it) }
            }


            if (bankingTransaction == null) return clear()

            amountView.text = (bankingTransaction.amount ?: BigDecimal.ZERO).format10Digit() + " " + fiatSymbol
            feeView.text =
                "Fee: " + (bankingTransaction.commission ?: BigDecimal.ZERO).format10Digit() + " " + fiatSymbol

            createdView.text = bankingTransaction.createdAt?.let { sdfDate.format(it) } ?: ""
            modifiedView.text = (bankingTransaction.modifiedAt)?.let { "Updated on: " + sdfTime.format(it) } ?: ""


            when (bankingTransaction.type) {
                "cashin" -> {
                    addressView.text = bankingTransaction.bankingId?.pan?.panSecurityMask() ?: ""
                    amountView.text =
                        "+ " + (bankingTransaction.amount ?: BigDecimal.ZERO).format10Digit() + " " + fiatSymbol
                }
                "cashout" -> {
                    addressView.text = bankingTransaction.bankingId?.iban?.ibanSecurityMask() ?: ""
                    amountView.text =
                        "- " + (bankingTransaction.amount ?: BigDecimal.ZERO).format10Digit() + " " + fiatSymbol
                }
            }

            when {
                bankingTransaction.error != null -> {
                    statusView.text = "Error"
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_red))
                }
                bankingTransaction.type == "cashin" && bankingTransaction.transactionId == null -> {
                    statusView.text = "Pay now!"
                    statusView.setTextColor(parent.context.resources.getColor(R.color.yellow))
                }
                bankingTransaction.type == "cashin" && bankingTransaction.referenceId == null -> {
                    statusView.text = "Waiting to be confirmed..."
                    statusView.setTextColor(parent.context.resources.getColor(R.color.colorSecondary))
                }
                bankingTransaction.type == "cashin" && bankingTransaction.referenceId != null -> {
                    statusView.text = "Done"
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_green))
                }
                bankingTransaction.type == "cashout" && bankingTransaction.referenceId == null -> {
                    statusView.text = "Will be paid soon..."
                    statusView.setTextColor(parent.context.resources.getColor(R.color.colorSecondary))
                }
                bankingTransaction.type == "cashout" && bankingTransaction.referenceId != null -> {
                    statusView.text = "Done"
                    statusView.setTextColor(parent.context.resources.getColor(R.color.real_green))
                }
                else -> {
                    statusView.text = "Unknown"
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
        }
    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<BankingTransaction>() {
            override fun areContentsTheSame(oldItem: BankingTransaction, newItem: BankingTransaction): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: BankingTransaction, newItem: BankingTransaction): Boolean =
                oldItem.id == newItem.id
        }
    }
}
