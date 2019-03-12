package com.stacrypt.stadroid.market.book

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Book

import kotlinx.android.synthetic.main.fragment_market_book.view.*
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar


class MarketBookRecyclerViewAdapter(
    var items: List<Pair<Book?, Book?>>
) : RecyclerView.Adapter<MarketBookRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_market_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val leftItem = items[position].first
        val rightItem = items[position].second
        holder.leftPriceView.text = leftItem?.price ?: ""
        holder.leftAmountView.text = leftItem?.amount ?: ""
        holder.rightPriceView.text = rightItem?.price ?: ""
        holder.rightAmountView.text = rightItem?.amount ?: ""
        holder.rightAmountView.text = rightItem?.amount ?: ""

        holder.leftProgressView.max = items.map { it.first?.amount?.toFloat() ?: 0F }.max()!!
        holder.leftProgressView.progress = leftItem?.amount?.toFloat() ?: 0F

        holder.rightProgressView.max = items.map { it.second?.amount?.toFloat() ?: 0F }.max()!!
        holder.rightProgressView.progress = rightItem?.amount?.toFloat() ?: 0F

//        val layerDrawable = holder.containerView.resources.getDrawable(R.drawable.test_shape) as LayerDrawable
//        val leftLayer = layerDrawable.findDrawableByLayerId(R.id.left) as Drawable
//        val rightLayer = layerDrawable.findDrawableByLayerId(R.id.right) as Drawable
//        layerDrawable.padd
//        leftLayer.setPadding(100, 0, 0, 0)
//        rightLayer.setPadding(0, 0, 100, 0)


//        val shape = LayerDrawable(
//            arrayOf(
//                holder.containerView.resources.getDrawable(R.drawable.test_shape).apply {setTint(Color.RED)},
//                holder.containerView.resources.getDrawable(R.drawable.test_shape).apply {setTint(Color.GREEN)}
//            )
//        ).apply {
//            setLayerSize(0, 100, 200)
//            setLayerGravity(0, Gravity.RIGHT)
//            setLayerGravity(1, Gravity.LEFT)
//            setLayerSize(1, 100, 200)
//            this.setLayerWidth()
//        }
//        holder.leftContainerView.backgroundColor = Color.RED
//        holder.leftContainerView.right = (holder.leftAmountView.width + holder.leftPriceView.width) - (leftItem?.amount?.toDouble()!! / 400.0 * (holder.leftAmountView.width + holder.leftPriceView.width)).toInt()
//        holder.rightContainerView.backgroundColor = Color.GREEN
//        holder.rightContainerView.left = (holder.rightPriceView.width + holder.rightAmountView.width) + (rightItem?.amount?.toDouble()!! / 400.0 * (holder.leftAmountView.width + holder.leftPriceView.width)).toInt()

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val leftProgressView: RoundCornerProgressBar = mView.bg_left
        val rightProgressView: RoundCornerProgressBar = mView.bg_right
        val leftPriceView: TextView = mView.price_left
        val leftAmountView: TextView = mView.amount_left
        val rightPriceView: TextView = mView.price_right
        val rightAmountView: TextView = mView.amount_right
    }
}
