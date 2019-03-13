package com.stacrypt.stadroid.market

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.stacrypt.stadroid.data.Market
import kotlinx.android.synthetic.main.fragment_market_summary.view.*
import com.stacrypt.stadroid.data.Kline
import kotlinx.android.synthetic.main.fragment_market_summary.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.stacrypt.stadroid.R


const val SHOWING_ITEMS = 20

class MarketSummaryFragment : Fragment() {
    private lateinit var viewModel: MarketViewModel
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_market_summary, container, false)!!
        return rootView
    }

    private fun initDataset(items: List<Kline>?): LineDataSet? {
        val values = ArrayList<Entry>()
        items?.forEachIndexed { i, it ->
            values.add(
                Entry(
                    i.toFloat(), (it.l + it.h).toFloat() / 2f
                )
            )
        }
        if (values.isNullOrEmpty()) return null
        val dataset = LineDataSet(values, "")
        dataset.setDrawIcons(false)
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataset.cubicIntensity = 0.2f
        dataset.colors = listOf(resources.getColor(R.color.colorPrimaryLight))
        dataset.valueTextSize = 0f
        dataset.fillAlpha = 255
        dataset.setDrawFilled(true)
        dataset.setDrawCircleHole(false)
        dataset.setDrawCircles(false)
        dataset.fillColor = resources.getColor(R.color.colorPrimaryLight)
        dataset.highLightColor = resources.getColor(R.color.colorPrimaryLight)
        dataset.setDrawCircleHole(false)
        dataset.fillFormatter = IFillFormatter { _, _ ->
            // change the return value here to better understand the effect
            // return 0;
            mini_chart.axisLeft.axisMinimum
        }

        return dataset
    }

    private fun initChart(dataset: LineDataSet?) {
        // Set dataset
        if (dataset != null) {
            mini_chart.data = LineData(dataset)
            mini_chart.notifyDataSetChanged()
            //        mini_chart.setVisibleYRangeMinimum(1_000F, YAxis.AxisDependency.RIGHT)
//        mini_chart.setVisibleYRangeMaximum(10_000F, YAxis.AxisDependency.RIGHT)
//        mini_chart.setVisibleYRangeMinimum(1_000F, YAxis.AxisDependency.LEFT)
//        mini_chart.setVisibleYRangeMaximum(10_000F, YAxis.AxisDependency.LEFT)
            mini_chart.invalidate()
//        mini_chart.setMaxVisibleValueCount(100)
//        mini_chart.zoom(1F, 1F, 100F, 100F)
            // scaling can now only be done on x- and y-axis separately
            mini_chart.setPinchZoom(true)
            mini_chart.isDoubleTapToZoomEnabled = false
            mini_chart.isNestedScrollingEnabled = false
            mini_chart.isHorizontalScrollBarEnabled = false
            mini_chart.isVerticalScrollBarEnabled = false
            mini_chart.setVisibleXRange(SHOWING_ITEMS.toFloat(), SHOWING_ITEMS.toFloat())
            mini_chart.moveViewToX((dataset.values.size - SHOWING_ITEMS / 2).toFloat())
            mini_chart.description.isEnabled = false
            mini_chart.isScaleYEnabled = false
            mini_chart.isScaleXEnabled = false
        }

        // Colors
        mini_chart.setBackgroundColor(resources.getColor(R.color.colorPrimary))

        // Touch
        mini_chart.requestDisallowInterceptTouchEvent(false)
        mini_chart.axisRight.isEnabled = false
        mini_chart.axisLeft.isEnabled = false
        mini_chart.xAxis.isEnabled = false
        mini_chart.isHighlightPerDragEnabled = false

        mini_chart.setDrawBorders(false)
        mini_chart.setBorderWidth(0F)
//        mini_chart.setBorderColor(resources.getColor(R.color.colorLightGray))
        mini_chart.legend.isEnabled = false
        mini_chart.isFocusableInTouchMode = false
        mini_chart.setTouchEnabled(false)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.market.observe(this, Observer<Market?> { market ->
            if (market == null) return@Observer
            rootView?.name?.text = market.name.replace("_", " / ")
        })

        viewModel.summary.observe(this, Observer {
            rootView?.open?.text = it?.low24.toString()
            rootView?.high?.text = it?.high24.toString()
            rootView?.low?.text = it?.low24.toString()
            rootView?.close?.text = it?.last24.toString()
            rootView?.volume?.text =
                it?.volume24.toString() + " " + (viewModel?.marketName?.value?.split("_")?.get(1) ?: "")
        })

        viewModel.last.observe(this, Observer {
            rootView?.price?.text = it?.price.toString()
        })

        viewModel.status.observe(this, Observer {
            //TODO
        })

        viewModel.kline.observe(this, Observer {
            initChart(initDataset(it))
        })
    }


}
