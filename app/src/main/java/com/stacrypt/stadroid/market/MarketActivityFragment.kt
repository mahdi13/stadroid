package com.stacrypt.stadroid.market

import android.graphics.Color
import android.graphics.Paint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.fragment_market.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.XAxis




/**
 * A placeholder fragment containing a simple view.
 */
class MarketActivityFragment : Fragment() {

    fun initDataset() {
        chart.setBackgroundColor(Color.WHITE)

        chart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)

        chart.setDrawGridBackground(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val leftAxis = chart.axisLeft
//        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
//        rightAxis.setStartAtZero(false);

//        // setting data
//        seekBarX.setProgress(40)
//        seekBarY.setProgress(100)

        chart.legend.isEnabled = false


        chart.resetTracking()

        val values = ArrayList<CandleEntry>()

        for (i in 0 until 100) {
//            val multi = seekBarY.getProgress() + 1
            val multi = i + 1
            val `val` = (Math.random() * 40).toFloat() + multi

            val high = (Math.random() * 9).toFloat() + 8f
            val low = (Math.random() * 9).toFloat() + 8f

            val open = (Math.random() * 6).toFloat() + 1f
            val close = (Math.random() * 6).toFloat() + 1f

            val even = i % 2 == 0

            values.add(
                CandleEntry(
                    i.toFloat(), `val` + high,
                    `val` - low,
                    if (even) `val` + open else `val` - open,
                    if (even) `val` - close else `val` + close
//                    resources.getDrawable(R.drawable.star)
                )
            )
        }

        val dataset = CandleDataSet(values, "Data Set")

        dataset.setDrawIcons(false)
        dataset.axisDependency = YAxis.AxisDependency.LEFT
        dataset.shadowColor = Color.DKGRAY
        dataset.shadowWidth = 0.7f
        dataset.decreasingColor = Color.RED
        dataset.decreasingPaintStyle = Paint.Style.FILL
        dataset.increasingColor = Color.rgb(122, 242, 84)
        dataset.increasingPaintStyle = Paint.Style.STROKE
        dataset.neutralColor = Color.BLUE


        dataset.colors = ColorTemplate.COLORFUL_COLORS.toList()
        chart.data = CandleData(dataset)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataset()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_market, container, false)
    }
}
