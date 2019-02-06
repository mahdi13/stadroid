package com.stacrypt.stadroid.market

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Book

import kotlinx.android.synthetic.main.fragment_market_book.view.*
import android.graphics.drawable.GradientDrawable


class MarketBookRecyclerViewAdapter(
    var items: List<Pair<Book?, Book?>>
) : RecyclerView.Adapter<MarketBookRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_market_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isLeft = position % 2 == 0
        val item = if (isLeft) items[position].first else items[position].second
        holder.priceView.text = item?.price ?: ""
        holder.amountView.text = item?.amount ?: ""

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setColor(Color.WHITE)
        shape.setStroke(2, Color.BLACK)
        holder.containerView.background = shape

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val containerView: View = mView.container
        val priceView: TextView = mView.price
        val amountView: TextView = mView.amount
    }
}
