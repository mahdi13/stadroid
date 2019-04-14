package io.stacrypt.stadroid.wallet.balance

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
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.iconResource
import kotlinx.android.synthetic.main.balance_detail_fragment.*

class BalanceDetailFragment : Fragment() {

    private lateinit var viewModel: BalanceDetailViewModel
    private lateinit var adapter: BalanceDetailPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        viewModel.currency.observe(viewLifecycleOwner, Observer { item ->
            if (item == null) return@Observer
            subject.text = "${item.name}'s Wallet"
            header_icon.setImageResource(item.iconResource()!!)
        })

        viewModel.balance.observe(viewLifecycleOwner, Observer<BalanceOverview> { item ->
            if (item == null) return@Observer
            header_title.text = "Your ${item.assetName} Balance:"
            header_amount.text = item.available.format10Digit()
            header_value.text = item.assetName // FIXME It should be the value

            deposit.setOnClickListener {
                if (activity is BalanceDetailActivity)
                    (activity as BalanceDetailActivity).showDeposit(item.assetName)
            }
            withdraw.setOnClickListener {
                if (activity is BalanceDetailActivity)
                    (activity as BalanceDetailActivity).showWithdraw(item.assetName)
            }
            history.setOnClickListener {
                if (activity is BalanceDetailActivity)
                    (activity as BalanceDetailActivity).showHistory(item.assetName)
            }
        })

        list.layoutManager = LinearLayoutManager(activity)

        back.setOnClickListener {
            (activity as BalanceDetailActivity).finish()
        }
    }
}
