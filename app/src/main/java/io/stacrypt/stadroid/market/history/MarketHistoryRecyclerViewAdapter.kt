package io.stacrypt.stadroid.market.history

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Deal
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.formatChangePercentFrom

import kotlinx.android.synthetic.main.fragment_market_history.view.*
import org.jetbrains.anko.textColorResource

class MarketHistoryRecyclerViewAdapter(
    var items: List<Deal>
) : RecyclerView.Adapter<MarketHistoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_market_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.amountView.text = item.amount.format10Digit()
        holder.priceView.text = item.price.format10Digit()

        val newPrice = item.price
        val lastPrice = if (position > 0) items[position - 1].price else newPrice
        val changePercent = newPrice.formatChangePercentFrom(lastPrice)

        holder.changeView.text = String.format("%.${2}f", changePercent)

        if (lastPrice > newPrice) {
            holder.priceView.textColorResource = R.color.real_red
            holder.changeView.textColorResource = R.color.real_red
        } else {
            holder.priceView.textColorResource = R.color.real_green
            holder.changeView.textColorResource = R.color.real_green
        }

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val amountView: TextView = mView.amount
        val priceView: TextView = mView.price
        val changeView: TextView = mView.change
    }
}
