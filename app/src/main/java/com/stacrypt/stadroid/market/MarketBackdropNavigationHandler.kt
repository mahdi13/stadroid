package com.stacrypt.stadroid.market

import android.animation.ObjectAnimator
import android.app.Activity
import android.util.DisplayMetrics
import android.graphics.drawable.Drawable
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Market
import org.jetbrains.annotations.Nullable


class MarketBackdropNavigationHandler @JvmOverloads internal constructor(
    context: Context,
    private val sheet: View,
    @param:Nullable private val interpolator: Interpolator? = null,
    @param:Nullable private val openIcon: Drawable? = null,
    @param:Nullable private val closeIcon: Drawable? = null,
    @param:Nullable private val toggleView: TextView? = null,
    @param:Nullable private val sheetList: RecyclerView? = null,
    @param:Nullable private val loadingText: String? = null
) : View.OnClickListener {

    private val duration = 500L

    private val animatorSet = AnimatorSet()
    private val height: Int
    private var backdropShown = false

    private var marketViewModel: MarketViewModel

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        marketViewModel = ViewModelProviders.of(context as AppCompatActivity).get(MarketViewModel::class.java)
        height = displayMetrics.heightPixels

        toggleView?.text = loadingText
        toggleView?.setOnClickListener(this)

        sheetList?.adapter = MarketListRecyclerViewAdapter(listOf()) {
            marketViewModel.marketName.setValue(it)
            collapse()
        }
        sheetList?.layoutManager = LinearLayoutManager(context)

        marketViewModel.allMarkets.observe(context, Observer<List<Market>> { markets ->
            if (markets == null) return@Observer

            val marketListAdapter = sheetList?.adapter as MarketListRecyclerViewAdapter?
            marketListAdapter?.items = markets
            marketListAdapter?.notifyDataSetChanged()

//            if (marketViewModel.marketName.value == null && markets.isNotEmpty())
//            if (markets.isNotEmpty())
//                marketViewModel.marketName.setValue(markets[0].name)
        })

        marketViewModel.marketName.observe(context, Observer { marketName ->
            toggleView?.text = marketName
        })

    }

    override fun onClick(view: View) = animateToggle(duration)

    private fun animateToggle(duration: Long) {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

//        updateIcon(view.childrenRecursiveSequence().findLast { it is ImageView }!!)

//        val translateY = height - Utils.convertDpToPixel(300.0F)
        val translateY = sheet.resources.getDimensionPixelSize(R.dimen.backdrop_height).toFloat()

        val animator = ObjectAnimator.ofFloat(sheet, "translationY", if (backdropShown) translateY else 0F)
        animator.duration = duration
        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)
        animator.start()
    }

    fun collapse(fast: Boolean = false) =
        if (backdropShown) animateToggle(if (fast) 1L else duration) else null

    fun expand() = if (!backdropShown) this.animateToggle(duration) else null

    private fun updateIcon(view: View) {
        if (openIcon != null && closeIcon != null) {
            if (view !is ImageView) {
                throw IllegalArgumentException("updateIcon() must be called on an ImageView")
            }
            if (backdropShown) {
                view.setImageDrawable(closeIcon)
            } else {
                view.setImageDrawable(openIcon)
            }
        }
    }
}
