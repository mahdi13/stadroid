package io.stacrypt.stadroid.wallet.ui.balancedetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BalanceHistory
import io.stacrypt.stadroid.data.BalanceOverview
import kotlinx.android.synthetic.main.balance_detail_fragment.*

class BalanceDetailFragment : Fragment() {

    private lateinit var viewModel: BalanceDetailViewModel
    private lateinit var adapter: BalanceDetailPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.balance_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BalanceDetailViewModel::class.java)
        adapter = BalanceDetailPagedAdapter(viewModel.balance.value)
        list.adapter = adapter

        viewModel.balanceHistory.observe(this, Observer<PagedList<BalanceHistory>> {
            adapter.submitList(it)
        })
        viewModel.balance.observe(this, Observer<BalanceOverview> {
            adapter.balanceOverview = it
            adapter.notifyDataSetChanged()
        })
        list.layoutManager = LinearLayoutManager(activity)


        adapter.onDepositClicked = {
            // TODO
        }
        adapter.onWithdrawClicked = {
            // TODO
        }
    }

}
