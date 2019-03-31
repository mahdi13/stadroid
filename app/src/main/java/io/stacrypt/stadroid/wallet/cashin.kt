package io.stacrypt.stadroid.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.UserRepository
import io.stacrypt.stadroid.profile.banking.BankingRepository
import io.stacrypt.stadroid.wallet.data.WalletRepository

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

        return rootView
    }
}