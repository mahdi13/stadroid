package io.stacrypt.stadroid.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ACTION_ADD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_ADD_BANK_CARD
import io.stacrypt.stadroid.profile.banking.BankCardPagedAdapter
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import io.stacrypt.stadroid.wallet.fiat.PaymentGatewayAdapter
import kotlinx.android.synthetic.main.frgment_cashin.view.*
import org.jetbrains.anko.support.v4.startActivity

class CashinViewModel : ViewModel() {

    val fiatSymbol = MutableLiveData<String>()

    val currency = Transformations.switchMap(fiatSymbol) { WalletRepository.getCurrency(it) }
    val paymentGateways = Transformations.switchMap(fiatSymbol) { WalletRepository.getPaymentGateways(it) }
//    val bankCards = Transformations.switchMap(fiatSymbol) { BankingRepository.getBankCards()}

}

class CashinFragment : Fragment() {
    lateinit var viewModel: CashinViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frgment_cashin, container, false)

        viewModel = ViewModelProviders.of(this).get(CashinViewModel::class.java)

        // FIXME: Empty observer to LiveData switchMaps work fine
        viewModel.currency.observe(viewLifecycleOwner, Observer { })
        viewModel.paymentGateways.observe(viewLifecycleOwner, Observer { items ->
            view?.payment_gateways?.adapter =
                PaymentGatewayAdapter(items.filter { it.fiatSymbol == viewModel.fiatSymbol.value })
        })
        viewModel.fiatSymbol.postValue(arguments?.getString(ARG_ASSET)!!)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.cards.adapter = BankCardPagedAdapter()

        view.add.setOnClickListener {
            startActivity<ProfileSettingActivity>(
                ProfileSettingActivity.ARG_TARGET to TARGET_ADD_BANK_CARD,
                ProfileSettingActivity.ARG_ACTION to ACTION_ADD
            )
        }
    }
}