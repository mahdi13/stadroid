package io.stacrypt.stadroid.wallet.fiat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.gson.Gson
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankCard
import io.stacrypt.stadroid.data.PaymentGateway
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ACTION_CHOOSE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_ACTION
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_LAUNCH_MODE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.LAUNCH_MODE_DIALOG
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.RESULT_CHOOSE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_ADD_BANK_CARD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import io.stacrypt.stadroid.profile.banking.BankingRepository
import io.stacrypt.stadroid.profile.banking.CurrencyTextWatcher
import io.stacrypt.stadroid.ui.calculateDepositCommission
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.fragment_transaction_detail.*
import kotlinx.android.synthetic.main.frgment_cashin.view.*
import kotlinx.android.synthetic.main.row_bank_card.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception
import java.math.BigDecimal

class CashinViewModel : ViewModel() {

    val fiatSymbol = MutableLiveData<String>()

    val currency = Transformations.switchMap(fiatSymbol) { WalletRepository.getCurrency(it) }
    val paymentGateways = Transformations.switchMap(fiatSymbol) { WalletRepository.getPaymentGateways(it) }
    val bankCards = Transformations.switchMap(fiatSymbol) {
        BankingRepository.getBankCards(it).pagedList
    }

    val selectedPaymentGateway = MutableLiveData<PaymentGateway?>()
    val selectedAmount = MutableLiveData<BigDecimal?>()
    val selectedCard = MutableLiveData<BankCard?>()
}

class CashinFragment : Fragment() {
    lateinit var viewModel: CashinViewModel
    private var amountTextWatcher: TextWatcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frgment_cashin, container, false)

        viewModel = ViewModelProviders.of(this).get(CashinViewModel::class.java)

        // FIXME: Empty observer to LiveData switchMaps work fine
        viewModel.currency.observe(viewLifecycleOwner, Observer {
            if (it != null && amountTextWatcher == null) {
                amountTextWatcher = CurrencyTextWatcher(it, rootView.amount, viewModel.selectedAmount)
                rootView.amount.addTextChangedListener(amountTextWatcher)
            }
        })

        viewModel.paymentGateways.observe(viewLifecycleOwner, Observer { items ->
            val paymentGatewayAdapter =
                PaymentGatewayAdapter(items.filter { it.fiatSymbol == viewModel.fiatSymbol.value }) {
                    viewModel.selectedPaymentGateway.postValue(it)
                }
            view?.payment_gateways?.adapter = paymentGatewayAdapter
        })
        viewModel.fiatSymbol.postValue(arguments?.getString(ARG_ASSET)!!)

        rootView.add_card.setOnClickListener {
            startActivity<ProfileSettingActivity>(
                ProfileSettingActivity.ARG_TARGET to TARGET_ADD_BANK_CARD,
                ProfileSettingActivity.ARG_LAUNCH_MODE to LAUNCH_MODE_DIALOG
            )
        }

        rootView.selected_card?.card_card?.setOnClickListener {
            startActivityForResult(Intent(context, ProfileSettingActivity::class.java).apply {
                putExtra(ARG_TARGET, TARGET_BANK_CARDS)
                putExtra(ARG_ACTION, ACTION_CHOOSE)
                putExtra(ARG_LAUNCH_MODE, LAUNCH_MODE_DIALOG)
            }, 0)
        }

        rootView.back.setOnClickListener {
            (activity as BalanceDetailActivity).up()
        }

        rootView.submit.setOnClickListener {
            if (viewModel.currency.value == null)
                return@setOnClickListener Unit.apply { toast("Wait or components to load completely") }

            if (viewModel.selectedAmount.value == null)
                return@setOnClickListener Unit.apply { toast("Amount is not valid") }

            if (viewModel.selectedPaymentGateway.value == null)
                return@setOnClickListener Unit.apply { toast("Please choose a payment gateway") }

            if (viewModel.selectedCard.value == null)
                return@setOnClickListener Unit.apply { toast("Please choose a card first") }

            if (viewModel.currency.value?.depositMax?.takeIf { it > BigDecimal(0) }?.takeIf { it < viewModel.selectedAmount.value } != null)
                return@setOnClickListener Unit.apply {
                    longToast(
                        "Amount is too high. Maximum amount is ${viewModel.currency.value?.depositMax?.format(
                            viewModel.currency.value!!
                        )}"
                    )
                }

            if (viewModel.currency.value?.depositMin?.takeIf { it > viewModel.selectedAmount.value } != null)
                return@setOnClickListener Unit.apply {
                    longToast(
                        "Amount is too low. Minimum is ${viewModel.currency.value?.depositMin?.format(
                            viewModel.currency.value!!
                        )}"
                    )
                }

            alert {
                ctx.setTheme(R.style.AlertDialogCustom)
                title = "Please review your deposit info:"
                message =
                    "Amount: ${viewModel.selectedAmount.value!!.format(viewModel.currency.value!!)}" +
                        "Commission: ${viewModel.currency?.value!!.calculateDepositCommission(viewModel.selectedAmount.value!!).format(
                            viewModel.currency.value!!
                        )}" +
                        "\n" + "You will pay by: ${viewModel.selectedPaymentGateway.value!!.name}" +
                        "\n" + "You SHOULD use card: ${viewModel.selectedCard.value!!.pan}"
                positiveButton("Let's do it") {
                    rootView.submit.startAnimation {
                        GlobalScope.launch(Dispatchers.Main) {
                            try {
                                val result = stemeraldApiClient.createShaparakIn(
                                    paymentGatewayName = viewModel.selectedPaymentGateway.value?.name!!,
                                    amount = viewModel.selectedAmount.value?.format(viewModel.currency.value!!)!!,
                                    bankCardId = viewModel.selectedCard.value?.id!!
                                ).await()


                                rootView.submit.doneLoadingAnimation(
                                    resources.getColor(R.color.real_green),
                                    resources.getDrawable(R.drawable.ic_check_circle_black_24dp).toBitmap(100, 100)
                                )

                                (activity as BalanceDetailActivity).showtransaction(result.id)

                                // Redirect him to the payment page on browser
                            } catch (e: Exception) {
                                // TODO: Handle balance error
                                e.printStackTrace()
                                toast(R.string.problem_occurred_toast)
                                // TODO: Stop submit button's animation and restart it
                            }
                        }
                    }
                }
                negativeButton("Cancel") {}
            }.show()

        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateCardLayout(null)
    }

    private fun populateCardLayout(bankCard: BankCard?) {
        if (bankCard == null) {
            view?.selected_card?.card_title?.text = "Choose a card"
            view?.selected_card?.pan?.text = ""
            view?.selected_card?.holder?.text = ""
        } else {
            view?.selected_card?.card_title?.text = "Card Number ${bankCard.id}"
            view?.selected_card?.pan?.text = bankCard.pan
            view?.selected_card?.holder?.text = bankCard.holder
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data?.getStringExtra(RESULT_CHOOSE) != null) {
            val card = Gson().fromJson(data.getStringExtra(RESULT_CHOOSE), BankCard::class.java)
            if (card != null) {
                viewModel.selectedCard.postValue(card)
                longToast("You selected ${card.pan}")
                populateCardLayout(card)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}