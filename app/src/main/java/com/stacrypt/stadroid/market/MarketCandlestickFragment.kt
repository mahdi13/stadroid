package com.stacrypt.stadroid.market


import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.utils.ColorTemplate

import com.stacrypt.stadroid.data.Kline
import kotlinx.android.synthetic.main.fragment_market_candlestick.*
import com.stacrypt.stadroid.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val SHOWING_ITEMS = 20

/**
 * A simple [Fragment] subclass.
 * Use the [MarketCandlestickFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MarketCandlestickFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: MarketViewModel

    private fun initDataset(items: List<Kline>?): CandleDataSet? {
        val values = ArrayList<CandleEntry>()

        items?.forEachIndexed { i, it ->
            values.add(
                CandleEntry(
                    i.toFloat(),
                    it.h.toFloat(),
                    it.l.toFloat(),
                    it.o.toFloat(),
                    it.c.toFloat()
                )
            )
        }

        if (values.isNullOrEmpty()) return null

        val dataset = CandleDataSet(values, "")

        dataset.setDrawIcons(false)
        dataset.axisDependency = YAxis.AxisDependency.LEFT
        dataset.shadowColorSameAsCandle = true
        dataset.shadowWidth = 1f
        dataset.valueTextColor = Color.WHITE
        dataset.decreasingColor = resources.getColor(R.color.pink_A400)
        dataset.decreasingPaintStyle = Paint.Style.FILL
        dataset.increasingColor = resources.getColor(R.color.green_A400)
        dataset.increasingPaintStyle = Paint.Style.FILL
        dataset.neutralColor = Color.BLUE

        dataset.colors = ColorTemplate.VORDIPLOM_COLORS.toList()

        return dataset
    }

    private fun initChart(dataset: CandleDataSet?) {
        // Set dataset
        if (dataset != null) {
            chart.data = CandleData(dataset)
            chart.notifyDataSetChanged()
            //        chart.setVisibleYRangeMinimum(1_000F, YAxis.AxisDependency.RIGHT)
//        chart.setVisibleYRangeMaximum(10_000F, YAxis.AxisDependency.RIGHT)
//        chart.setVisibleYRangeMinimum(1_000F, YAxis.AxisDependency.LEFT)
//        chart.setVisibleYRangeMaximum(10_000F, YAxis.AxisDependency.LEFT)
            chart.invalidate()
//        chart.setMaxVisibleValueCount(100)
//        chart.zoom(1F, 1F, 100F, 100F)
            // scaling can now only be done on x- and y-axis separately
            chart.setPinchZoom(true)
            chart.isDoubleTapToZoomEnabled = false
            chart.isNestedScrollingEnabled = true
            chart.isHorizontalScrollBarEnabled = true
            chart.isVerticalScrollBarEnabled = true
            chart.setVisibleXRange(SHOWING_ITEMS.toFloat(), SHOWING_ITEMS.toFloat())
            chart.moveViewToX((dataset.values.size - SHOWING_ITEMS / 2).toFloat())
            chart.description.isEnabled = false
            chart.isScaleYEnabled = false
            chart.isScaleXEnabled = true
        }

        // Colors
        chart.setBackgroundColor(resources.getColor(R.color.colorPrimary))

        // Touch
        chart.requestDisallowInterceptTouchEvent(true)

        // xAxis
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)// disable x axis grid lines
        xAxis.setDrawLabels(true)
//        xAxis.axisMinimum = (dataset.values.size - 25).toFloat()
//        xAxis.axisMaximum = dataset.values.size.toFloat()
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.axisLineColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.setValueFormatter { value, axis ->
            "${((value * 5) / 60).toInt() % 24}:${(value * 5).toInt() % 60}"
        }

        // Right Axis
        chart.axisRight.isEnabled = false

        // Left axis
        val leftAxis = chart.axisLeft
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisLineColor = Color.WHITE
        leftAxis.textColor = Color.WHITE



        chart.isHighlightPerDragEnabled = true

        chart.setDrawBorders(false)
        chart.setBorderWidth(0F)

//        chart.setBorderColor(resources.getColor(R.color.colorLightGray))


        chart.axisRight.textColor = Color.WHITE

        chart.legend.isEnabled = false


    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.kline.observe(this,
            Observer<List<Kline>> { klineItems ->
                // FIXME: This method is called several times and reload the existing data
                initChart(initDataset(klineItems))
                //                adapter.items = balances!!
//                adapter.notifyDataSetChanged()
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market_candlestick, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MarketCandlestickFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MarketCandlestickFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
