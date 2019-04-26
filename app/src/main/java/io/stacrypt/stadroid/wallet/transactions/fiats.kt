package io.stacrypt.stadroid.wallet.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.fragment_transaction_history_list.view.*
import kotlinx.android.synthetic.main.fragment_transactions_history.view.*
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import org.jetbrains.anko.support.v4.withArguments

const val ARG_FIAT_SYMBOL = "fiat"

class FiatTransactionsViewModel : ViewModel() {

    lateinit var fiatSymbol: String

    private val cashinsListing by lazy { WalletRepository.getBankingTransactions("cashin", fiatSymbol) }
    val cashins by lazy { cashinsListing.pagedList }
    val cashinsNetworkState by lazy { cashinsListing.networkState }
    val cashinsRefreshState by lazy { cashinsListing.refreshState }

    private val cashoutListing by lazy { WalletRepository.getBankingTransactions("cashout", fiatSymbol) }
    val cashouts by lazy { cashoutListing.pagedList }
    val cashoutsNetworkState by lazy { cashoutListing.networkState }
    val cashoutsRefreshState by lazy { cashoutListing.refreshState }
}

class CashinHistoryList : Fragment() {

    lateinit var adapter: BankingTransactionHistoryPagedAdapter
    lateinit var viewModel: FiatTransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_transaction_history_list, container, false)

        viewModel = ViewModelProviders.of(parentFragment!!).get(FiatTransactionsViewModel::class.java)
        viewModel.fiatSymbol = arguments?.getString(ARG_FIAT_SYMBOL)!!

        adapter = BankingTransactionHistoryPagedAdapter(viewModel.fiatSymbol)
        rootView.list.adapter = adapter
        rootView.list.layoutManager = LinearLayoutManager(context)

        viewModel.cashins.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        adapter.onItemClickListener = {
            (activity!! as BalanceDetailActivity).showBankingTransaction(it.paymentMethod.fiatSymbol, it.id)
        }
        return rootView
    }
}

class CashoutHistoryList : Fragment() {

    lateinit var adapter: BankingTransactionHistoryPagedAdapter
    lateinit var viewModel: FiatTransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_transaction_history_list, container, false)

        viewModel = ViewModelProviders.of(parentFragment!!).get(FiatTransactionsViewModel::class.java)
        viewModel.fiatSymbol = arguments?.getString(ARG_FIAT_SYMBOL)!!

        adapter = BankingTransactionHistoryPagedAdapter(viewModel.fiatSymbol)
        rootView.list.adapter = adapter
        rootView.list.layoutManager = LinearLayoutManager(context)

        viewModel.cashouts.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        adapter.onItemClickListener = {
            (activity!! as BalanceDetailActivity).showBankingTransaction(it.paymentMethod.fiatSymbol, it.id)
        }
        return rootView
    }
}

class FiatTransactions : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_transactions_history, container, false)

        rootView.viewpager.viewpager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> CashinHistoryList()
                    1 -> CashoutHistoryList()
                    else -> throw IndexOutOfBoundsException()
                }.apply {
                    this.withArguments(
                        ARG_FIAT_SYMBOL to this@FiatTransactions.arguments?.getString(
                            BalanceDetailActivity.ARG_ASSET
                        )
                    )
                }
            }

            override fun getPageTitle(position: Int): CharSequence? = when (position) {
                0 -> "Deposit History"
                1 -> "Withdraw History"
                else -> null
            }

            override fun getCount(): Int = 2
        }

        rootView.back.setOnClickListener {
            (activity as BalanceDetailActivity).up()
        }

        return rootView
    }
}