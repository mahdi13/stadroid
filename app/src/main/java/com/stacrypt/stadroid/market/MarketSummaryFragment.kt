package com.stacrypt.stadroid.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Market
import kotlinx.android.synthetic.main.fragment_market_summary.view.*


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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)
        viewModel.currentMarket.observe(this, Observer<Market?> { market ->
            if (market == null) return@Observer
            rootView?.name?.text = market.name
            rootView?.open?.text = market.summary?.low24.toString()
            rootView?.high?.text = market.summary?.high24.toString()
            rootView?.low?.text = market.summary?.low24.toString()
            rootView?.close?.text = market.summary?.last24.toString()
        })
    }


}
