package io.stacrypt.stadroid.market.history

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Deal
import io.stacrypt.stadroid.market.MarketViewModel
import kotlinx.android.synthetic.main.fragment_market_history_list.view.*

class MarketHistoryFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MarketHistoryRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_market_history_list, container, false)

        adapter = MarketHistoryRecyclerViewAdapter(ArrayList())
        rootView.list.layoutManager = LinearLayoutManager(context)
        rootView.list.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)

        viewModel.deal.observe(this,
            Observer<List<Deal>> { deals ->
                adapter.items = deals
                adapter.notifyDataSetChanged()
            })

    }
}
