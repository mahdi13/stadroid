package com.stacrypt.stadroid.market

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Mine


class MarketMineFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MarketMineRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_market_mine_list, container, false) as RecyclerView

        adapter = MarketMineRecyclerViewAdapter(ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return recyclerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)
        viewModel.mine.observe(this,
            Observer<List<Mine>> { mines ->
                adapter.items = mines
                adapter.notifyDataSetChanged()
            })

    }
}
