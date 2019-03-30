package io.stacrypt.stadroid.wallet.ui.balancedetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BalanceHistory
import io.stacrypt.stadroid.data.BalanceOverview
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.iconResource
import io.stacrypt.stadroid.wallet.BalanceDetailActivity
import kotlinx.android.synthetic.main.balance_detail_fragment.*
import kotlinx.android.synthetic.main.row_balance_detail_header.*
import kotlinx.android.synthetic.main.row_balance_detail_header.view.*
import kotlinx.android.synthetic.main.row_balance_detail_history.*
import kotlinx.android.synthetic.main.row_balance_detail_history.view.*

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
        adapter = BalanceDetailPagedAdapter()
        list.adapter = adapter

        viewModel.balanceHistory.observe(this, Observer<PagedList<BalanceHistory>> {
            adapter.submitList(it)
        })
        viewModel.balance.observe(this, Observer<BalanceOverview> { item ->

            subject.text = "${item.currency.name}'s Wallet"
            header_icon.setImageResource(item.currency.iconResource()!!)
            header_title.text = "Your ${item.assetName} Balance:"
            header_amount.text = item.available.format10Digit()
            header_value.text = item.assetName // FIXME It should be the value

            deposit.setOnClickListener {
                if (activity is BalanceDetailActivity)
                    (activity as BalanceDetailActivity).showDeposit()
            }
            withdraw.setOnClickListener {
                if (activity is BalanceDetailActivity)
                    (activity as BalanceDetailActivity).showWithdraw()
            }

        })

        list.layoutManager = LinearLayoutManager(activity)

    }

}
