package io.stacrypt.stadroid.wallet.cryptocurrency

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import androidx.databinding.*
import androidx.lifecycle.Observer
import io.stacrypt.stadroid.data.*
import io.stacrypt.stadroid.data.Currency
import io.stacrypt.stadroid.databinding.WithdrawFragmentBinding
import io.stacrypt.stadroid.ext.isValidBitcoinAddress
import io.stacrypt.stadroid.profile.banking.CurrencyTextWatcher
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import kotlinx.android.synthetic.main.withdraw_fragment.*
import kotlinx.android.synthetic.main.withdraw_fragment.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import retrofit2.HttpException
import java.math.BigDecimal
import java.util.*

class WithdrawViewModel : ObservableViewModel() {
    val cryptocurrencySymbol = MutableLiveData<String>()

    val currency: LiveData<Currency?> = switchMap(cryptocurrencySymbol) {
        WalletRepository.getCurrency(it)
    }

//    val currency: LiveData<Currency> by lazy {
//        switchMap(cryptocurrencySymbol) {
//            if (it == null) null
//            else WalletRepository.getCurrency(it)
//        }
//    }

    @Bindable
    val selectedAmount = MutableLiveData<String>()

    @Bindable
    val selectedAddress = MutableLiveData<String>()

    val withdrawResult = MutableLiveData<Event<Withdraw>>()
    val withdrawError = MutableLiveData<Event<String>>()

    val businessUid by lazy { UUID.randomUUID().toString() }

    // fun doWithdraw() =
    //     GlobalScope.launch(Dispatchers.IO) {
    //         try {
    //             withdrawResult.postValue(
    //                 Event(
    //                     stemeraldApiClient.scheduleWithdraw(
    //                         assetName = cryptocurrencySymbol.value!!,
    //                         amount = selectedAmount.value!!,
    //                         address = selectedAmount.value!!,
    //                         businessUid = businessUid
    //                     ).await()
    //                 )
    //             )
    //         } catch (e: HttpException) {
    //             e.printStackTrace()
    //             withdrawError.postValue(Event(e.verboseLocalizedMessage()))
    //             // TODO: Describe what is the problem by parsing the error result
    //         } catch (e: Exception) {
    //             e.printStackTrace()
    //             withdrawError.postValue(Event("Error occurred: ${e.message}"))
    //             // TODO: Describe what is the problem by parsing the error result
    //         }
    //     }
}

class WithdrawFragment : Fragment() {

    private lateinit var viewModel: WithdrawViewModel
    private var amountTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(WithdrawViewModel::class.java)

        val binding = DataBindingUtil.inflate<WithdrawFragmentBinding>(
            inflater, R.layout.withdraw_fragment, container, false
        )
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        // here data must be an instance of the class MarsDataProvider
//        binding.setA(data)

        view.submit.setOnClickListener {
            if (viewModel.currency.value == null)
                return@setOnClickListener Unit.apply { toast("Wait or components to load completely") }

            if (!validateInputs()) return@setOnClickListener

            val withdrawCommission = if (
                viewModel.currency.value?.withdrawCommissionRate == null ||
                viewModel.currency.value?.withdrawCommissionRate?.toBigDecimal() == BigDecimal(0)
            )
                BigDecimal(0)
            else
                BigDecimal(viewModel.selectedAmount.value).times(BigDecimal(viewModel.currency.value?.withdrawCommissionRate))

            alert {
                title = "Please review your withdrawal request:"
                message = StringBuilder()
                    .append("You are withdrawing ${viewModel.selectedAmount.value}").append("\n")

                    .append("Amount you will received: ")
                    .append(viewModel.selectedAmount.value!!.format(viewModel.currency.value!!))
                    .append("\n")

                    .append("Commission: (about)")
                    .append(withdrawCommission.format(viewModel.currency.value!!))
                    .append("\n")

                    .append("Network Fee: (about)")
                    .append("0.0") // TODO Calculate
                    .append("\n")

                    .append("You will receive this amount within maximum 1 hour").append("\n")
                    .append("Your ${viewModel.currency.value?.name} address is ${viewModel.selectedAmount.value}")
                    .append("\n")
                    .append("Your are agree with all terms and conditions .")
                    .append("\n")

                negativeButton("Cancel") { }
                positiveButton("Let's do it") {
                    view.submit.startAnimation {
                        GlobalScope.launch(Dispatchers.Main) {
                            try {
                                val result = stemeraldApiClient.scheduleWithdraw(
                                    assetName = viewModel.cryptocurrencySymbol.value!!,
                                    amount = viewModel.selectedAmount.value!!,
                                    address = viewModel.selectedAmount.value!!,
                                    businessUid = viewModel.businessUid
                                ).await()

                                view.submit.doneLoadingAnimation(
                                    resources.getColor(R.color.real_green),
                                    resources.getDrawable(R.drawable.ic_check_circle_black_24dp).toBitmap(100, 100)
                                )

                                (activity as BalanceDetailActivity).showtransaction(result.id)

                            } catch (e: HttpException) {
                                // TODO: Handle all errors
                                e.printStackTrace()
                                longToast(e.verboseLocalizedMessage())
                                view.submit.stopAnimation()
                                view.submit.revertAnimation()
                                view.submit.invalidate()
                                // TODO: Stop submit button's animation and restart it
                            } catch (e: Exception) {
                                // TODO: Handle all errors
                                e.printStackTrace()
                                toast(R.string.problem_occurred_toast)
                                view.submit.stopAnimation()
                                view.submit.revertAnimation()
                                view.submit.invalidate()
                                // TODO: Stop submit button's animation and restart it
                            }
                        }
                    }
                }
            }.show()
        }

        viewModel.currency.observe(viewLifecycleOwner, Observer {
            if (it != null && amountTextWatcher == null) {
                amountTextWatcher = CurrencyTextWatcher(it, view.amount, null)
                view.amount.addTextChangedListener(amountTextWatcher)
            }

        })

        viewModel.cryptocurrencySymbol.value = arguments?.getString(ARG_ASSET)!!
        viewModel.notifyChange()

        view.back.setOnClickListener {
            (activity as BalanceDetailActivity).up()
        }


        return view

//        return inflater.inflate(R.layout.withdraw_fragment, container, false)

//        viewModel.depositInfo.observe(this, Observer {
//            if (it == null) return@Observer
//            try {
//                val myBitmap = QRCode.from("www.example.org").bitmap()
//                val myImage = findViewById(R.id.imageView) as ImageView
//                myImage.setImageBitmap(myBitmap)
//            } catch (e: WriterException) { //eek }
//
//            }
//
//        })
    }

    private fun validateInputs(): Boolean {

        // Reset errors.
        address.error = null
        amount.error = null

        // Store values at the time of the login attempt.
        if (viewModel.selectedAmount.value?.toBigDecimalOrNull()
                ?.takeIf { it >= viewModel.currency.value?.withdrawMin ?: BigDecimal(0) }
                ?.takeIf {
                    viewModel.currency.value?.withdrawMax == null ||
                        viewModel.currency.value?.withdrawMax == BigDecimal(0) ||
                        it <= viewModel.currency.value?.withdrawMax
                } == null
        ) {
            amount.error = "Invalid amount"
            return false
        }

        if (
            viewModel.selectedAddress.value
                ?.takeIf { it.isValidBitcoinAddress(viewModel.cryptocurrencySymbol.value == "TBTC") } == null
        ) {
            address.error = "Invalid address"
            return false
        }

        return true
    }
}
