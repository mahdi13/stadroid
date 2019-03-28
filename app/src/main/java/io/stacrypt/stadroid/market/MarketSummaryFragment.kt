package io.stacrypt.stadroid.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.data.Market
import kotlinx.android.synthetic.main.fragment_market_summary.view.*
import io.stacrypt.stadroid.R


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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.market.observe(this, Observer<Market?> { market ->
            if (market == null) return@Observer
            rootView?.market_name?.text = market.name.replace("_", " / ")
        })

        viewModel.allMarkets.observe(this, Observer<List<Market>> { markets ->
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

    }


}
