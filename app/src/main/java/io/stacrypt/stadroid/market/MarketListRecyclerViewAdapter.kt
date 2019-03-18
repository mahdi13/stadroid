package io.stacrypt.stadroid.market

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Market
import kotlinx.android.synthetic.main.backdrop_market_row.view.*


class MarketListRecyclerViewAdapter(
    var items: List<Market>,
    val onMarketSelectedListener: (String) -> Unit
) : RecyclerView.Adapter<MarketListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.backdrop_market_row, parent, false) as Button
        view.setOnClickListener { onMarketSelectedListener(it.tag as String) }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.name.tag = item.name
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.name
    }
}
