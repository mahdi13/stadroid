package io.stacrypt.stadroid.profile.banking

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankAccount
import io.stacrypt.stadroid.data.BankCard
import kotlinx.android.synthetic.main.row_bank_account.view.*
import kotlinx.android.synthetic.main.row_bank_card.view.*

class BankCardPagedAdapter : PagedListAdapter<BankCard, BankCardPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((BankCard) -> Unit)? = null

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
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<BankCard>() {
            override fun areContentsTheSame(oldItem: BankCard, newItem: BankCard): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: BankCard, newItem: BankCard): Boolean =
                oldItem.id == newItem.id
        }
    }
}

class BankAccountPagedAdapter : PagedListAdapter<BankAccount, BankAccountPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((BankAccount) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_bank_account, parent, false)
    ) {
        val cardView: CardView = itemView.account_card
        val titleView: TextView = itemView.account_title
        val numberView: TextView = itemView.iban
        val ownerView: TextView = itemView.owner

        fun bindTo(bankAccount: BankAccount?) {
            cardView.setOnClickListener { v ->
                bankAccount?.let { onItemClickListener?.invoke(it) }
            }

            if (bankAccount == null) return clear()

            titleView.text = "Account Number ${bankAccount.id}"
            numberView.text = bankAccount.iban
            ownerView.text = bankAccount.owner
        }

        private fun clear() {
            titleView.text = ""
            numberView.text = ""
            ownerView.text = ""
        }
    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<BankAccount>() {
            override fun areContentsTheSame(oldItem: BankAccount, newItem: BankAccount): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: BankAccount, newItem: BankAccount): Boolean =
                oldItem.id == newItem.id
        }
    }
}
