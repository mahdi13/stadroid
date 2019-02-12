package com.stacrypt.stadroid.market

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.Gravity
import androidx.appcompat.widget.DrawableUtils
import androidx.constraintlayout.solver.widgets.Rectangle
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.rightPadding


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
        val leftContainerView: View = mView.bg_left
        val rightContainerView: View = mView.bg_right
        val leftPriceView: TextView = mView.price_left
        val leftAmountView: TextView = mView.amount_left
        val rightPriceView: TextView = mView.price_right
        val rightAmountView: TextView = mView.amount_right
    }
}
