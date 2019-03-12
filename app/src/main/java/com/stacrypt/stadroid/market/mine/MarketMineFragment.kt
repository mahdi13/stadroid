package com.stacrypt.stadroid.market.mine

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Mine
import com.stacrypt.stadroid.market.MarketViewModel
import kotlinx.android.synthetic.main.fragment_market_mine_list.view.*


class MarketMineFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MarketMineRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_market_mine_list, container, false)
        val recyclerView = rootView.list

        adapter = MarketMineRecyclerViewAdapter(ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.mine.observe(this,
            Observer<List<Mine>> { mines ->
                adapter.items = mines
                adapter.notifyDataSetChanged()
            })

    }
}
