package io.stacrypt.stadroid.wallet.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.DepositDetail

class CryptocurrencyDepositsPagedAdapter :
    PagedListAdapter<DepositDetail, CryptocurrencyDepositsPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((DepositDetail) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_bank_card, parent, false)
    ) {
        val cardView: CardView = itemView.card_card
        val titleView: TextView = itemView.card_title
        val numberView: TextView = itemView.pan
        val holderView: TextView = itemView.holder

        fun bindTo(bankCard: BankCard?) {
            cardView.setOnClickListener { v ->
                bankCard?.let { onItemClickListener?.invoke(it) }
            }

            if (bankCard == null) return clear()

            titleView.text = "Card Number ${bankCard.id}"
            numberView.text = bankCard.pan
            holderView.text = bankCard.holder
        }

        private fun clear() {
            titleView.text = ""
            numberView.text = ""
            holderView.text = ""
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
