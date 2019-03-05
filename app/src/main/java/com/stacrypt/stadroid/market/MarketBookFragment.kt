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
import com.stacrypt.stadroid.data.BookResponse
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
        viewModel.book.observe(this,
            Observer<BookResponse> { books ->
                val items = ArrayList<Pair<Book?, Book?>>()
                for (i in 0..(max(books.buys.size, books.sells.size))) {
                    items.add(
                        Pair(
                            if (books.buys.size > i) books.buys[i] else null,
                            if (books.sells.size > i) books.sells[i] else null
                        )
                    )
                }
                adapter.items = items
                adapter.notifyDataSetChanged()
            })

    }
}
