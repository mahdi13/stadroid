package io.stacrypt.stadroid.wallet.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.DepositDetail
import kotlinx.android.synthetic.main.row_deposit_history.view.*

class CryptocurrencyDepositsPagedAdapter :
    PagedListAdapter<DepositDetail, CryptocurrencyDepositsPagedAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((DepositDetail) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_deposit_history, parent, false)
    ) {
        val cardView: CardView = itemView.deposit_card
        val idView: TextView = itemView.deposit_id

        fun bindTo(depositDetail: DepositDetail?) {
            cardView.setOnClickListener { v ->
                depositDetail?.let { onItemClickListener?.invoke(it) }
            }

            if (depositDetail == null) return clear()

            idView.text = depositDetail.id
        }

        private fun clear() {
            idView.text = ""
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
