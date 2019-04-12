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
import kotlinx.android.synthetic.main.fragment_cryptocurrency_transaction_list.view.*
import kotlinx.android.synthetic.main.fragment_cryptocurrency_transactions.view.*
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import org.jetbrains.anko.support.v4.withArguments

const val ARG_CRYPTOCURRENCY_SYMBOL = "cryptocurrency"

class CryptocurrencyTransactionsViewModel : ViewModel() {

    lateinit var cryptocurrencySymbol: String
    private val depositsListing by lazy { WalletRepository.getDeposits(cryptocurrencySymbol) }
    val deposits by lazy { depositsListing.pagedList }
    val depositsNetworkState by lazy { depositsListing.networkState }
    val depositsRefreshState by lazy { depositsListing.refreshState }

    private val withdrawsListing by lazy { WalletRepository.getDeposits(cryptocurrencySymbol) }
    val withdraws by lazy { depositsListing.pagedList }
    val withdrawsNetworkState by lazy { depositsListing.networkState }
    val withdrawsRefreshState by lazy { depositsListing.refreshState }
}

class DepositCryptocurrencyTransactionList : Fragment() {

    lateinit var adapter: CryptocurrencyDepositsPagedAdapter
    lateinit var viewModel: CryptocurrencyTransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_cryptocurrency_transaction_list, container, false)

        viewModel = ViewModelProviders.of(parentFragment!!).get(CryptocurrencyTransactionsViewModel::class.java)
        viewModel.cryptocurrencySymbol = arguments?.getString(ARG_CRYPTOCURRENCY_SYMBOL)!!

        adapter = CryptocurrencyDepositsPagedAdapter()
        rootView.list.adapter = adapter
        rootView.list.layoutManager = LinearLayoutManager(context)

        viewModel.deposits.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        adapter.onItemClickListener = {
            (activity!! as BalanceDetailActivity).showtransaction(it.id)
        }
        return rootView
    }
}

class WithdrawCryptocurrencyTransactionList : Fragment() {

    lateinit var adapter: CryptocurrencyDepositsPagedAdapter
    lateinit var viewModel: CryptocurrencyTransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_cryptocurrency_transaction_list, container, false)

        viewModel = ViewModelProviders.of(parentFragment!!).get(CryptocurrencyTransactionsViewModel::class.java)
        viewModel.cryptocurrencySymbol = arguments?.getString(ARG_CRYPTOCURRENCY_SYMBOL)!!

        adapter = CryptocurrencyDepositsPagedAdapter()
        rootView.list.adapter = adapter
        rootView.list.layoutManager = LinearLayoutManager(context)

        viewModel.deposits.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return rootView
    }
}

class CryptocurrencyTransactions : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_cryptocurrency_transactions, container, false)

        rootView.viewpager.viewpager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> DepositCryptocurrencyTransactionList()
                    1 -> WithdrawCryptocurrencyTransactionList()
                    else -> throw IndexOutOfBoundsException()
                }.apply {
                    this.withArguments(
                        ARG_CRYPTOCURRENCY_SYMBOL to this@CryptocurrencyTransactions.arguments?.getString(
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