package io.stacrypt.stadroid.market.marketdeals

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.MarketDeal
import io.stacrypt.stadroid.market.MarketViewModel
import kotlinx.android.synthetic.main.fragment_market_deals_list.view.*

class MarketDealsFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MarketDealsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_market_deals_list, container, false)

        adapter = MarketDealsRecyclerViewAdapter(ArrayList()) {
            viewModel.newOrderPrice.postValue(it.price)
        }
        rootView.list.layoutManager = LinearLayoutManager(context)
        rootView.list.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)

        viewModel.marketDeals.observe(this,
            Observer<List<MarketDeal>> { deals ->
                adapter.items = deals
                adapter.notifyDataSetChanged()
            })
    }
}
