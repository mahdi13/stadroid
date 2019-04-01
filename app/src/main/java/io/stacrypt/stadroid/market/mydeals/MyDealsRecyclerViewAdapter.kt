package io.stacrypt.stadroid.market.mydeals

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.MyDeal
import io.stacrypt.stadroid.ui.format10Digit


import kotlinx.android.synthetic.main.fragment_my_deals.view.*
import org.jetbrains.anko.textColorResource


class MyDealsRecyclerViewAdapter(
    var items: List<MyDeal>
) : RecyclerView.Adapter<MyDealsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_my_deals, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.priceView.text = item.price.format10Digit()
        holder.amountView.text = item.amount.format10Digit()

        // TODO: Is it right?
        holder.valueView.text = item.amount.times(item.price).format10Digit().removeSuffix("0").removeSuffix(".")

        if (item.side.toLowerCase() == "buy")
            holder.amountView.textColorResource = R.color.real_green
        else
            holder.amountView.textColorResource = R.color.real_red

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val priceView: TextView = mView.price
        val amountView: TextView = mView.amount
        val valueView: TextView = mView.value
    }
}
