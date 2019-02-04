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
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.utils.ColorTemplate

import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Balance
import com.stacrypt.stadroid.data.Kline
import com.stacrypt.stadroid.wallet.AssetBalanceViewModel
import kotlinx.android.synthetic.main.fragment_market_candlestick.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

    private fun initDataset(items: List<Kline>?): CandleDataSet {
        val values = ArrayList<CandleEntry>()



        items?.forEachIndexed { i, it ->
            //            val multi = seekBarY.getProgress() + 1
            val multi = i + 1
            val `val` = (Math.random() * 40).toFloat() + multi

//            val high = (Math.random() * 9).toFloat() + 8f
//            val low = (Math.random() * 9).toFloat() + 8f
//
//            val open = (Math.random() * 6).toFloat() + 1f
//            val close = (Math.random() * 6).toFloat() + 1f


            val high = it.h.toFloat()
            val low = it.l.toFloat()

            val open = it.o.toFloat()
            val close = it.c.toFloat()

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

        return dataset
    }

    private fun initChart(dataset: CandleDataSet) {
        chart.setBackgroundColor(Color.WHITE)

        chart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)

        chart.setDrawGridBackground(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
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

        chart.data = CandleData(dataset)

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)
        viewModel.kline.observe(this,
            Observer<List<Kline>> { klineItems ->
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
