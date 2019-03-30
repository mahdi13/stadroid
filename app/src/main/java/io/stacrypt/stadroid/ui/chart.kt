package io.stacrypt.stadroid.ui

import android.graphics.Matrix
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.charts.Chart
import android.view.MotionEvent
import android.graphics.Matrix.MTRANS_X
import android.graphics.Matrix.MSCALE_X
import android.util.Log
import android.view.View
import com.github.mikephil.charting.listener.ChartTouchListener


class CoupleChartGestureListener(val srcChart: Chart<*>, private val dstCharts: Array<Chart<*>>) : OnChartGestureListener {
    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChartSingleTapped(me: MotionEvent?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChartLongPressed(me: MotionEvent?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChartDoubleTapped(me: MotionEvent?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
        //Log.d(TAG, "onChartScale " + scaleX + "/" + scaleY + " X=" + me.getX() + "Y=" + me.getY());
        syncCharts()
    }

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
        Log.d("salam", "onChartTranslate " + dX + "/" + dY + " X=" + me.getX() + "Y=" + me.getY());
        syncCharts()
    }

    fun syncCharts() {
        val srcMatrix: Matrix = srcChart.viewPortHandler.matrixTouch
        val srcVals = FloatArray(9)
        var dstMatrix: Matrix
        val dstVals = FloatArray(9)

        // get src chart translation matrix:
        srcMatrix.getValues(srcVals)

        // apply X axis scaling and position to dst charts:
        for (dstChart in dstCharts) {
            if (dstChart.visibility == View.VISIBLE) {
                dstMatrix = dstChart.viewPortHandler.matrixTouch
                dstMatrix.getValues(dstVals)
                dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X]
                dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X]
                dstMatrix.setValues(dstVals)
                dstChart.viewPortHandler.refresh(dstMatrix, dstChart, true)
            }
        }
    }

}