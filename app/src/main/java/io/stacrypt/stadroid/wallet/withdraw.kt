package io.stacrypt.stadroid.wallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import androidx.databinding.*
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.map
import io.stacrypt.stadroid.data.*
import io.stacrypt.stadroid.data.Currency
import io.stacrypt.stadroid.databinding.WithdrawFragmentBinding
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.withdraw_fragment.*
import kotlinx.android.synthetic.main.withdraw_fragment.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
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
    val amount = MutableLiveData<String>()

    val estimatedCommission by lazy {
        map(amount) {
            Log.d(
                "salam",
                "salamamdlksfsdjfjdskdalkdjsblnf;s'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            )
            if (
                currency.value?.withdrawCommissionRate == null ||
                currency.value?.withdrawCommissionRate?.toBigDecimal() == BigDecimal(0)
            )
                BigDecimal(0)
            else
                BigDecimal(amount.value).times(BigDecimal(currency.value?.withdrawCommissionRate))
        }
    }

    @Bindable
    val address = MutableLiveData<String>()

    val withdrawResult = MutableLiveData<Event<Withdaraw>>()
    val withdrawError = MutableLiveData<Event<String>>()

    private val businessUid by lazy { UUID.randomUUID().toString() }

    fun doWithdraw() =
        GlobalScope.launch(Dispatchers.IO) {
            try {
                withdrawResult.postValue(
                    Event(
                        stemeraldApiClient.scheduleWithdraw(
                            assetName = cryptocurrencySymbol.value!!,
                            amount = amount.value!!,
                            address = address.value!!,
                            businessUid = businessUid
                        ).await()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                withdrawError.postValue(Event("Error occurred: ${e.message}"))
                // TODO: Describe what is the problem by parsing the error result
            }
        }
}

class WithdrawFragment : Fragment() {

    private lateinit var viewModel: WithdrawViewModel

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

        view.submit.onClick {
            if (!validateInputs()) return@onClick
            alert {
                customView {
                    verticalLayout {
                        textView("You are withdrawing ${viewModel.amount.value}")
                        textView(
                            "You will be charged (about) ${viewModel.amount.value} " +
                                    "+ ${viewModel.estimatedCommission.value?.format10Digit()} (fee)"
                        )
                        textView("You will receive this amount within maximum 1 hour")
                        textView("Your ${viewModel.currency.value?.name} address is ${viewModel.address.value}")
                        textView("Your are agree with all terms and conditions.")
                        padding = dip(16)
                    }
                }
                negativeButton("Cancel") { }
                positiveButton("Let's do it") {
                    viewModel.doWithdraw()
                }
            }.show()
        }

        // FIXME: Empty observer to LiveData switchMaps work fine
        viewModel.currency.observe(viewLifecycleOwner, Observer { })
        viewModel.estimatedCommission.observe(viewLifecycleOwner, Observer { })

        viewModel.cryptocurrencySymbol.value = arguments?.getString(ARG_ASSET)!!
        viewModel.notifyChange()

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
        if (viewModel.amount.value?.toBigDecimalOrNull()
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

        if (viewModel.address.value?.isNotEmpty()?.takeIf { true /*TODO: Validate address */ } != true) {
            address.error = "Invalid address"
            return false
        }

        return true
    }
}

open class ObservableViewModel : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.remove(callback)
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}
