package io.stacrypt.stadroid.market.myorders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Order
import io.stacrypt.stadroid.market.MarketViewModel
import kotlinx.android.synthetic.main.fragment_my_orders_list.view.*

class MyOrdersFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MyOrdersRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_orders_list, container, false)
        val recyclerView = rootView.list

        adapter = MyOrdersRecyclerView(ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.myOrders.observe(this,
            Observer<List<Order>> { mines ->
                adapter.items = mines
                adapter.notifyDataSetChanged()
            })

    }
}
