package io.stacrypt.stadroid.market.book

import android.os.Bundle
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
import io.stacrypt.stadroid.market.MarketViewModel
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.fragment_market_book_list.view.*
import kotlin.math.max

class MarketBookFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MarketBookRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_market_book_list, container, false)

        adapter = MarketBookRecyclerViewAdapter(ArrayList()) { book ->
            book?.price?.let { viewModel.newOrderPrice.postValue(book.price) }
        }
        rootView.list.layoutManager = LinearLayoutManager(context)
        rootView.list.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.book.observe(
            this,
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

                if (books.buys.isNotEmpty() && books.sells.isNotEmpty()) {
                    view?.spread?.text =
                        (books.sells.map { it.price }.min()!! - books.buys.map { it.price }.max()!!).format10Digit() +
                            " " + (viewModel.marketName.split("_")?.get(0) ?: "")
                }
            })
    }
}
