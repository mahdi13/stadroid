package io.stacrypt.stadroid.ui

import android.content.Context
import android.view.MotionEvent
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager


class UnscrollableViewPager : ViewPager {

    private var isPagingEnabled = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPagingEnabled(b: Boolean) {
        this.isPagingEnabled = b
    }
}
