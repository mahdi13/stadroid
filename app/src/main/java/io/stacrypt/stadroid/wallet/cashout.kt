package io.stacrypt.stadroid.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.profile.banking.BankCardPagedAdapter
import io.stacrypt.stadroid.profile.banking.BankingRepository
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.frgment_cashout.view.*

class CashoutViewModel : ViewModel() {

    private val fiatSymbol = MutableLiveData<String>()

    fun init(fiatSymbol: String) {
        this.fiatSymbol.value = fiatSymbol
    }

    val currency = Transformations.switchMap(fiatSymbol) {
        WalletRepository.getCurrency(it)
    }
    val paymentGateways = Transformations.switchMap(fiatSymbol) { WalletRepository.getPaymentGateways(it) }
    val bankAccounts = BankingRepository.getBankAccounts()

}

class CashoutFragment : Fragment() {
    lateinit var viewModel: CashoutViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frgment_cashout, container, false)

        viewModel = ViewModelProviders.of(this).get(CashoutViewModel::class.java)

        // FIXME: Empty observer to LiveData switchMaps work fine
        viewModel.currency.observe(viewLifecycleOwner, Observer { })

        viewModel.init(arguments?.getString(ARG_ASSET)!!)
        rootView.bank_accounts.adapter = BankCardPagedAdapter()


        return rootView
    }
}