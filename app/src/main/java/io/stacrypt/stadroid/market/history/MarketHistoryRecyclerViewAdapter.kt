package io.stacrypt.stadroid.market.history

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Deal

import kotlinx.android.synthetic.main.fragment_market_history.view.*
import org.jetbrains.anko.textColorResource
import kotlin.math.abs

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
        holder.amountView.text = item.amount
        holder.priceView.text = item.price

        val newPrice = item.price.toDouble()
        val lastPrice = if (position > 0) items[position - 1].price.toDouble() else newPrice
        val change = newPrice - lastPrice
        val changePercent = abs(change) / lastPrice * 100.0

        holder.changeView.text = String.format("%.${2}f", changePercent)

        if (change >= 0) {
            holder.priceView.textColorResource = R.color.pink_600
            holder.changeView.textColorResource = R.color.pink_600
        } else {
            holder.priceView.textColorResource = R.color.green_600
            holder.changeView.textColorResource = R.color.green_600
        }

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val amountView: TextView = mView.amount
        val priceView: TextView = mView.price
        val changeView: TextView = mView.change
    }
}
