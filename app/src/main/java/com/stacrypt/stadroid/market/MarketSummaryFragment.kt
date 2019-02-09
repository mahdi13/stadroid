package com.stacrypt.stadroid.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Deal


class MarketSummaryFragment : Fragment() {
    private lateinit var viewModel: MarketViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_market_summary, container, false)!!


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)
        viewModel.deal.observe(this,
            Observer<List<Deal>> { deals ->
                adapter.items = deals
                adapter.notifyDataSetChanged()
            })

    }


}
