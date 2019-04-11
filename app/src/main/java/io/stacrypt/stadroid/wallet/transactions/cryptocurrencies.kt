package io.stacrypt.stadroid.wallet.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.paging.DataSource
import com.google.android.material.tabs.TabLayout
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Listing
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.activity_market.*
import kotlinx.android.synthetic.main.fragment_cryptocurrency_transaction_list.view.*
import kotlinx.android.synthetic.main.fragment_cryptocurrency_transactions.view.*

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

        adapter = CryptocurrencyDepositsPagedAdapter()
        rootView.list.adapter = adapter
        viewModel.deposits.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return rootView
    }
}

class WithdrawCryptocurrencyTransactionList : Fragment() {

    lateinit var adapter: CryptocurrencyDepositsPagedAdapter
    lateinit var viewModel: CryptocurrencyTransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_cryptocurrency_transaction_list, container, false)

        viewModel = ViewModelProviders.of(parentFragment!!).get(CryptocurrencyTransactionsViewModel::class.java)

        adapter = CryptocurrencyDepositsPagedAdapter()
        rootView.list.adapter = adapter
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
                }.apply { this.arguments?.putString(ARG_CRYPTOCURRENCY_SYMBOL, this@CryptocurrencyTransactions.arguments?.getString(
                    ARG_CRYPTOCURRENCY_SYMBOL))!! }
            }

            override fun getCount(): Int = 6
        }

        rootView.tabs.setupWithViewPager(rootView.viewpager)

        return rootView
    }
}