package io.stacrypt.stadroid.wallet

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import io.stacrypt.stadroid.data.BalanceOverview
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.iconResource
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ACTION_DEPOSIT
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ACTION_HISTORY
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ACTION_WITHDRAW
import kotlinx.android.synthetic.main.fragment_asset_balance.view.*

class WalletAdapter(
    var items: List<BalanceOverview>
) : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

//    private val mOnClickListener: View.OnClickListener

//    init {
//        mOnClickListener = View.OnClickListener { v ->
//            val item = v.tag as DummyItem
    // Notify the active callbacks interface (the activity, if the fragment is attached to
    // one) that an item has been selected.
//            mListener?.onListFragmentInteraction(item)
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.tag = item.assetName
        holder.nameView.text = item.currency.name
        holder.symbolView.text = item.currency.symbol
//        holder.totalView.text = (item.available + item.freeze).toString()
        holder.availableView.text = item.available.format10Digit()
        holder.freezeView.text = item.freeze.format10Digit()
        holder.iconView.setImageResource(item.currency.iconResource()!!)

        holder.itemView.tag = item.assetName
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_asset_balance, parent, false)
    ) {

        val symbolView: TextView = itemView.symbol
        val nameView: TextView = itemView.name
        val cardView: View = itemView.card
        val depositView: Button = itemView.deposit
        val withdrawView: Button = itemView.withdraw
        val historyView: Button = itemView.history
        //        val totalView: TextView = itemView.available
        val availableView: TextView = itemView.total
        val freezeView: TextView = itemView.freeze
        val iconView: ImageView = itemView.icon

        init {
            cardView.setOnClickListener {
                it.context.startActivity(
                    BalanceDetailActivity.createIntent(itemView.context, itemView.tag as String)
                )
            }
            depositView.setOnClickListener {
                it.context.startActivity(
                    BalanceDetailActivity.createIntent(
                        itemView.context,
                        itemView.tag as String,
                        ACTION_DEPOSIT
                    )
                )
            }
            withdrawView.setOnClickListener {
                it.context.startActivity(
                    BalanceDetailActivity.createIntent(
                        itemView.context,
                        itemView.tag as String,
                        ACTION_WITHDRAW
                    )
                )
            }
            historyView.setOnClickListener {
                it.context.startActivity(
                    BalanceDetailActivity.createIntent(
                        itemView.context,
                        itemView.tag as String,
                        ACTION_HISTORY
                    )
                )
            }
        }
    }
}
