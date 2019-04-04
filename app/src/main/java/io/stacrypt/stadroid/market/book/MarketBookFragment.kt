package io.stacrypt.stadroid.market.book

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Book
import io.stacrypt.stadroid.data.BookResponse
import io.stacrypt.stadroid.data.Market
import io.stacrypt.stadroid.market.MarketViewModel
import kotlin.math.max


class MarketBookFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MarketBookRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_market_book_list, container, false) as RecyclerView

        adapter = MarketBookRecyclerViewAdapter(ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return recyclerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.market.observe(viewLifecycleOwner,
            Observer<Market> { market ->

            })

    }
}
