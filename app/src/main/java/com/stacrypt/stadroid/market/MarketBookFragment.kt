package com.stacrypt.stadroid.market

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.Book


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
        viewModel = ViewModelProviders.of(this).get(MarketViewModel::class.java)
        viewModel.book.observe(this,
            Observer<List<Book>> { books ->
                val items = ArrayList<Pair<Book?, Book?>>()
                for (i in 0..(books.size / 2 + 1)) {
                    items.add(
                        Pair(
                            books.filter { it.side == "buy" }.findLast { it.i == i },
                            books.filter { it.side == "sell" }.findLast { it.i == i }
                        )
                    )
                }
                adapter.items = items
                adapter.notifyDataSetChanged()
            })

    }
}
