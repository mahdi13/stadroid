package io.stacrypt.stadroid.market.marketdeals

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.MarketDeal
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.formatChangePercentFrom

import kotlinx.android.synthetic.main.fragment_market_deals.view.*
import org.jetbrains.anko.textColorResource
import java.text.SimpleDateFormat
import java.util.*

class MarketDealsRecyclerViewAdapter(
    var items: List<MarketDeal>
) : RecyclerView.Adapter<MarketDealsRecyclerViewAdapter.ViewHolder>() {

    private val sdf = SimpleDateFormat("hh:mm", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_market_deals, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.amountView.text = item.amount.format10Digit()
        holder.priceView.text = item.price.format10Digit()
        holder.timeView.text = sdf.format(item.time)

        val newPrice = item.price
        val lastPrice = if (position > 0) items[position - 1].price else newPrice

        holder.changeView.text = newPrice.formatChangePercentFrom(lastPrice)

        if (lastPrice > newPrice) {
            holder.changeView.textColorResource = R.color.real_red
        } else {
            holder.changeView.textColorResource = R.color.real_green
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val amountView: TextView = mView.amount
        val priceView: TextView = mView.price
        val changeView: TextView = mView.change
        val timeView: TextView = mView.time
    }
}
