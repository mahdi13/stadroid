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
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.market.observe(this, Observer<Market?> { market ->
            if (market == null) return@Observer
            rootView?.name?.text = market.name
        })

        viewModel.summary.observe(this, Observer {
            rootView?.open?.text = it?.low24.toString()
            rootView?.high?.text = it?.high24.toString()
            rootView?.low?.text = it?.low24.toString()
            rootView?.close?.text = it?.last24.toString()
        })

        viewModel.last.observe(this, Observer {
            rootView?.price?.text = it?.price.toString()
        })

        viewModel.status.observe(this, Observer {
            //TODO
        })
    }


}
