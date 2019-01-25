package com.stacrypt.stadroid.market

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.graphics.Color
import kotlinx.android.synthetic.main.fragment_market_summary.*


private const val ARG_MARKET_NAME = "market"

class MarketSummaryFragment : Fragment() {
    private var marketName: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private fun setupChart() {
        chart.isHighlightPerDragEnabled = true
        chart.setDrawBorders(true)
        chart.setBorderColor(Color.parseColor("#999999"))

        val yAxis = chart.axisLeft
        val rightAxis = chart.axisRight
        yAxis.setDrawGridLines(false)
        rightAxis.setDrawGridLines(false)
        chart.requestDisallowInterceptTouchEvent(true)

        val xAxis = chart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines
        xAxis.setDrawLabels(false)
        rightAxis.textColor = Color.WHITE
        yAxis.setDrawLabels(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val l = chart.legend
        l.isEnabled = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            marketName = it.getString(ARG_MARKET_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.stacrypt.stadroid.R.layout.fragment_market_summary, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
        setupChart()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param  marketName Name of the market
         * @return A new instance of fragment MarketSummaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(marketName: String) =
            MarketSummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MARKET_NAME, marketName)
                }
            }
    }
}
