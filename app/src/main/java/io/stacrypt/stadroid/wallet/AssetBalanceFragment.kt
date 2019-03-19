package io.stacrypt.stadroid.wallet

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.data.BalanceOverview


class AssetBalanceFragment : Fragment() {

    private lateinit var viewModel: AssetBalanceViewModel
    private lateinit var adapter: AssetBalanceRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_asset_balance_list, container, false) as RecyclerView

        adapter = AssetBalanceRecyclerViewAdapter(ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return recyclerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AssetBalanceViewModel::class.java)
        viewModel.balances.observe(this,
            Observer<List<BalanceOverview>> { balances ->
                adapter.items = balances!!
                adapter.notifyDataSetChanged()
            })

    }

}