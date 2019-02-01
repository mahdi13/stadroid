package com.stacrypt.stadroid.wallet

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.stacrypt.stadroid.R

import androidx.lifecycle.Observer
import com.stacrypt.stadroid.data.Balance


class AssetBalanceFragment : Fragment() {

    private lateinit var viewModel: AssetBalanceViewModel
    private lateinit var adapter: AssetBalanceRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_asset_balance_list, container, false) as RecyclerView

        adapter = AssetBalanceRecyclerViewAdapter(ArrayList())
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.balances.observe(this,
            Observer<List<Balance>> { balances -> adapter.items = balances!! })


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AssetBalanceViewModel::class.java)
    }

}
