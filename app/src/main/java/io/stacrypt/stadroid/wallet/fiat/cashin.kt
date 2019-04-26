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
import io.stacrypt.stadroid.data.PaymentMethod
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.data.verboseLocalizedMessage
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ACTION_CHOOSE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_ACTION
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_LAUNCH_MODE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.LAUNCH_MODE_DIALOG
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.RESULT_CHOOSE
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_ADD_BANK_CARD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import io.stacrypt.stadroid.profile.banking.CurrencyTextWatcher
import io.stacrypt.stadroid.ui.calculateCashinCommission
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.ui.formatForJson
import io.stacrypt.stadroid.ui.showError
import io.stacrypt.stadroid.ui.showSuccess
import io.stacrypt.stadroid.ui.showWarning
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.frgment_cashin.view.*
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import kotlinx.android.synthetic.main.row_bank_card.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.HttpException
import java.lang.Exception
import java.math.BigDecimal

class CashinViewModel : ViewModel() {

    val fiatSymbol = MutableLiveData<String>()

    val currency = Transformations.switchMap(fiatSymbol) { WalletRepository.getCurrency(it) }
    val paymentMethods = Transformations.switchMap(fiatSymbol) { WalletRepository.getPaymentMethods(it) }

    val selectedPaymentMethod = MutableLiveData<PaymentMethod?>()
    val selectedAmount = MutableLiveData<BigDecimal?>()
    val selectedCard = MutableLiveData<BankCard?>()
}

class CashinFragment : Fragment() {
    lateinit var viewModel: CashinViewModel
    private var amountTextWatcher: TextWatcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frgment_cashin, container, false)

        viewModel = ViewModelProviders.of(this).get(CashinViewModel::class.java)

        viewModel.currency.observe(viewLifecycleOwner, Observer {
            if (it != null && amountTextWatcher == null) {
                amountTextWatcher = CurrencyTextWatcher(it, rootView.amount, viewModel.selectedAmount)
                rootView.amount.addTextChangedListener(amountTextWatcher)
            }
        })

        viewModel.paymentMethods.observe(viewLifecycleOwner, Observer { items ->
            val paymentMethodAdapter =
                PaymentMethodAdapter(items.filter { it.fiatSymbol == viewModel.fiatSymbol.value }) {
                    viewModel.selectedPaymentMethod.postValue(it)
                }
            view?.payment_gateways?.adapter = paymentMethodAdapter
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
            // TODO: Disable the submit button to prevent recreate the transaction

            if (viewModel.currency.value == null)
                return@setOnClickListener Unit.apply { toast("Wait or components to load completely") }

            if (viewModel.selectedAmount.value == null)
                return@setOnClickListener Unit.apply { toast("Amount is not valid") }

            if (viewModel.selectedPaymentMethod.value == null)
                return@setOnClickListener Unit.apply { toast("Please choose a payment gateway") }

            if (viewModel.selectedCard.value == null)
                return@setOnClickListener Unit.apply { toast("Please choose a card first") }

            if (viewModel.selectedCard.value?.isVerified != true)
                return@setOnClickListener Unit.apply { longToast("The selected card is not verified yet. Usually it should be done in a few hours...") }

            if (viewModel.selectedPaymentMethod.value?.cashinMax?.takeIf { it > BigDecimal(0) }?.takeIf { it < viewModel.selectedAmount.value } != null)
                return@setOnClickListener Unit.apply {
                    longToast(
                        "Amount is too high. Maximum amount is ${viewModel.selectedPaymentMethod.value?.cashinMax?.format(
                            viewModel.currency.value!!
                        )}"
                    )
                }

            if (viewModel.selectedPaymentMethod.value?.cashinMin?.takeIf { it > viewModel.selectedAmount.value } != null)
                return@setOnClickListener Unit.apply {
                    longToast(
                        "Amount is too low. Minimum is ${viewModel.selectedPaymentMethod.value?.cashinMin?.format(
                            viewModel.currency.value!!
                        )}"
                    )
                }

            alert {
                title = "Please review your deposit info:"
                message =
                    "Amount: ${viewModel.selectedAmount.value!!.format(viewModel.currency.value!!)}" + "\n" +
                        "Commission: ${viewModel.selectedPaymentMethod?.value!!.calculateCashinCommission(viewModel.selectedAmount.value!!).format(
                            viewModel.currency.value!!
                        )}" +
                        "\n" + "You will pay by: ${viewModel.selectedPaymentMethod.value!!.gateway}" +
                        "\n" + "You SHOULD use card: ${viewModel.selectedCard.value!!.pan}"
                positiveButton("Let's do it") {
                    rootView.submit.startAnimation {
                        GlobalScope.launch(Dispatchers.Main) {
                            try {
                                val result = stemeraldApiClient.createCashin(
                                    paymentMethodId = viewModel.selectedPaymentMethod.value?.id!!,
                                    amount = viewModel.selectedAmount.value?.formatForJson(viewModel.currency.value!!)!!,
                                    bankingIdId = viewModel.selectedCard.value?.id!!
                                ).await()


                                rootView.submit.doneLoadingAnimation(
                                    resources.getColor(R.color.real_green),
                                    resources.getDrawable(R.drawable.ic_check_circle_black_24dp).toBitmap(100, 100)
                                )

                                (activity as BalanceDetailActivity).showBankingTransaction(
                                    result.paymentMethod.fiatSymbol,
                                    result.id
                                )

                                // Redirect him to the payment page on browser
                            } catch (e: HttpException) {
                                // TODO: Handle all errors
                                e.printStackTrace()
                                longToast(e.verboseLocalizedMessage())
                                rootView.submit.stopAnimation()
                                rootView.submit.revertAnimation()
                                rootView.submit.invalidate()
                                // TODO: Stop submit button's animation and restart it
                            } catch (e: Exception) {
                                // TODO: Handle all errors
                                e.printStackTrace()
                                toast(R.string.problem_occurred_toast)
                                rootView.submit.stopAnimation()
                                rootView.submit.revertAnimation()
                                rootView.submit.invalidate()
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
            view?.selected_card?.card_verification?.showWarning("")
        } else {
            view?.selected_card?.card_title?.text = "Card # ${bankCard.id}"
            view?.selected_card?.pan?.text = bankCard.pan
            view?.selected_card?.holder?.text = bankCard.holder
            view?.selected_card?.card_verification?.apply {
                when {
                    bankCard.isVerified -> showSuccess("Verified")
                    bankCard.error != null -> showError("Rejected: ${bankCard.error}")
                    else -> showWarning("Waiting to be verified...")
                }
            }
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