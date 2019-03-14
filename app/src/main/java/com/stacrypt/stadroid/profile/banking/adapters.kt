    package com.stacrypt.stadroid.profile.banking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.BankCard
import kotlinx.android.synthetic.main.row_bank_card.view.*

class BankCardPagedAdapter : PagedListAdapter<BankCard, BankCardPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))


    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_bank_card, parent, false)
    ) {
        val titleView: TextView = itemView.title
        val numberView: TextView = itemView.number
        val holderView: TextView = itemView.holder

        fun bindTo(bankCard: BankCard?) {
            if (bankCard == null) return clear()

            titleView.text = "Card Number ${bankCard.id.toString()}"
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