package io.stacrypt.stadroid.market.mydeals

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.MyDeal


import kotlinx.android.synthetic.main.fragment_my_deals.view.*
import java.text.SimpleDateFormat


class MyDealsRecyclerViewAdapter(
    var items: List<MyDeal>
) : RecyclerView.Adapter<MyDealsRecyclerViewAdapter.ViewHolder>() {

    private val sdf = SimpleDateFormat("dd-MM hh:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_my_deals, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.priceView.text = item.price.toString()
        holder.amountView.text = item.amount.toString()
//        holder.feeView.text = item.fee
//        holder.dateView.text = sdf.format(item.time)
//
//        holder.statusView.text = "Active"
//        holder.statusView.textColorResource = R.color.greenLight
//
//        if (item.side == "1") {
//            holder.sideView.text = "Bye"
//            holder.sideView.textColorResource = R.color.greenLight
//        } else {
//            holder.sideView.text = "Sell"
//            holder.sideView.textColorResource = R.color.redLight
//        }
//
//        if (item.role == "1") {
//            holder.roleView.text = "Maker"
//        } else {
//            holder.roleView.text = "Taker"
//        }

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val priceView: TextView = mView.price
        val amountView: TextView = mView.amount
//        val feeView: TextView = mView.fee
//        val statusView: TextView = mView.status
//        val dateView: TextView = mView.date
//        val editView: TextView = mView.edit
//        val roleView: TextView = mView.role
//        val sideView: TextView = mView.side
    }
}
