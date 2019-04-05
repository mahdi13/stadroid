package io.stacrypt.stadroid.market.mydeals

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.MyDeal
import io.stacrypt.stadroid.market.MarketViewModel
import kotlinx.android.synthetic.main.fragment_my_deals_list.view.*

class MarketMyDealsFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel
    private lateinit var adapter: MyDealsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_deals_list, container, false)
        val recyclerView = rootView.list

        adapter = MyDealsRecyclerViewAdapter(ArrayList()){
            viewModel.newOrderPrice.postValue(it.price)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.myDeals.observe(this,
            Observer<List<MyDeal>> { mines ->
                adapter.items = mines
                adapter.notifyDataSetChanged()
            })
    }
}
