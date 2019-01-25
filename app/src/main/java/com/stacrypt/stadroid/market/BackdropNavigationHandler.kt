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
import com.github.mikephil.charting.utils.Utils
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.annotations.Nullable


class BackdropNavigationHandler @JvmOverloads internal constructor(
    context: Context,
    private val sheet: View, @param:Nullable private val interpolator: Interpolator? = null,
    @param:Nullable private val openIcon: Drawable? = null, @param:Nullable private val closeIcon: Drawable? = null
) : View.OnClickListener {

    private val animatorSet = AnimatorSet()
    private val height: Int
    private var backdropShown = false

    init {

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
    }

    override fun onClick(view: View) {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        updateIcon(view.childrenRecursiveSequence().findLast { it is ImageView }!!)

//        val translateY = height - Utils.convertDpToPixel(300.0F)
        val translateY = 200F

        val animator = ObjectAnimator.ofFloat(sheet, "translationY", if (backdropShown) translateY else 0F)
        animator.duration = 500
        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)
        animator.start()
    }

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
