package com.stacrypt.stadroid.market

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Deal

import kotlinx.android.synthetic.main.fragment_market_history.view.*

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
        holder.mIdView.text = item.id
        holder.mContentView.text = item.content
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content
    }
}
