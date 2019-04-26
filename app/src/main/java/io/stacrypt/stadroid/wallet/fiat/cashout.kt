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
import io.stacrypt.stadroid.data.BankAccount
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
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_ADD_BANK_ACCOUNT
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_ACCOUNTS
import io.stacrypt.stadroid.profile.banking.CurrencyTextWatcher
import io.stacrypt.stadroid.ui.calculateCashoutCommission
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.ui.formatForJson
import io.stacrypt.stadroid.ui.showError
import io.stacrypt.stadroid.ui.showSuccess
import io.stacrypt.stadroid.ui.showWarning
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity.Companion.ARG_ASSET
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.frgment_cashout.view.*
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import kotlinx.android.synthetic.main.row_bank_account.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import java.math.BigDecimal
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.HttpException

class CashoutViewModel : ViewModel() {

    val fiatSymbol = MutableLiveData<String>()

    val currency = Transformations.switchMap(fiatSymbol) {
        WalletRepository.getCurrency(it)
    }
    val paymentMethods = Transformations.switchMap(fiatSymbol) { WalletRepository.getPaymentMethods(it) }

    val selectedPaymentMethod = MutableLiveData<PaymentMethod?>()
    val selectedAmount = MutableLiveData<BigDecimal?>()
    val selectedAccount = MutableLiveData<BankAccount?>()
}

class CashoutFragment : Fragment() {
    lateinit var viewModel: CashoutViewModel
    private var amountTextWatcher: TextWatcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frgment_cashout, container, false)

        viewModel = ViewModelProviders.of(this).get(CashoutViewModel::class.java)

        viewModel.currency.observe(viewLifecycleOwner, Observer {
            if (it != null && amountTextWatcher == null) {
                amountTextWatcher = CurrencyTextWatcher(it, rootView.amount, viewModel.selectedAmount)
                rootView.amount.addTextChangedListener(amountTextWatcher)
            }
        })

        viewModel.paymentMethods.observe(viewLifecycleOwner, Observer { items ->
            val paymentMethodAdapter =
                PaymentMethodAdapter(items.filter { it.fiatSymbol == viewModel.fiatSymbol.value && it.cashoutable }) {
                    viewModel.selectedPaymentMethod.postValue(it)
                }
            view?.payment_gateways?.adapter = paymentMethodAdapter
        })
        viewModel.fiatSymbol.postValue(arguments?.getString(ARG_ASSET)!!)

        rootView.add_account.setOnClickListener {
            startActivity<ProfileSettingActivity>(
                ProfileSettingActivity.ARG_TARGET to TARGET_ADD_BANK_ACCOUNT,
                ProfileSettingActivity.ARG_LAUNCH_MODE to LAUNCH_MODE_DIALOG
            )
        }

        rootView.selected_account?.account_card?.setOnClickListener {
            startActivityForResult(Intent(context, ProfileSettingActivity::class.java).apply {
                putExtra(ARG_TARGET, TARGET_BANK_ACCOUNTS)
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

            if (viewModel.selectedPaymentMethod.value == null)
                return@setOnClickListener Unit.apply { toast("Please choose a payment gateway") }

            if (viewModel.selectedAccount.value == null)
                return@setOnClickListener Unit.apply { toast("Please choose an account first") }

            if (viewModel.selectedAccount.value?.isVerified != true)
                return@setOnClickListener Unit.apply { longToast("The selected account is not verified yet. Usually it should be done in a few hours...") }

            if (viewModel.selectedPaymentMethod.value?.cashoutMax?.takeIf { it > BigDecimal(0) }?.takeIf { it < viewModel.selectedAmount.value } != null)
                return@setOnClickListener Unit.apply {
                    longToast(
                        "Amount is too high. Maximum amount is ${viewModel.selectedPaymentMethod.value?.cashoutMax?.format(
                            viewModel.currency.value!!
                        )}"
                    )
                }

            if (viewModel.selectedPaymentMethod.value?.cashoutMin?.takeIf { it > viewModel.selectedAmount.value } != null)
                return@setOnClickListener Unit.apply {
                    longToast(
                        "Amount is too low. Minimum is ${viewModel.selectedPaymentMethod.value?.cashoutMin?.format(
                            viewModel.currency.value!!
                        )}"
                    )
                }

            alert {
                title = "Please review your withdraw info:"
                message =
                    "Amount: ${viewModel.selectedAmount.value!!.format(viewModel.currency.value!!)}" + "\n" +
                        "Commission: ${viewModel.selectedPaymentMethod?.value!!.calculateCashoutCommission(viewModel.selectedAmount.value!!).format(
                            viewModel.currency.value!!
                        )}" +
                        "\n" + "You will be paid by: ${viewModel.selectedPaymentMethod.value!!.gateway}" +
                        "\n" + "Will be received on account: ${viewModel.selectedAccount.value!!.iban}"
                positiveButton("Let's do it") {
                    rootView.submit.startAnimation {
                        GlobalScope.launch(Dispatchers.Main) {
                            try {
                                val result = stemeraldApiClient.scheduleCashout(
                                    paymentMethodId = viewModel.selectedPaymentMethod.value?.id!!,
                                    amount = viewModel.selectedAmount.value?.formatForJson(viewModel.currency.value!!)!!,
                                    bankingIdId = viewModel.selectedAccount.value?.id!!
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
        populateAccountLayout(null)
    }

    private fun populateAccountLayout(bankAccount: BankAccount?) {
        if (bankAccount == null) {
            view?.selected_account?.account_title?.text = "Choose an account"
            view?.selected_account?.iban?.text = ""
            view?.selected_account?.owner?.text = ""
        } else {
            view?.selected_account?.account_title?.text = "Account # ${bankAccount.id}"
            view?.selected_account?.iban?.text = bankAccount.iban
            view?.selected_account?.owner?.text = bankAccount.owner
            view?.selected_account?.account_verification?.apply {
                when {
                    bankAccount.isVerified -> showSuccess("Verified")
                    bankAccount.error != null -> showError("Rejected: ${bankAccount.error}")
                    else -> showWarning("Waiting to be verified...")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data?.getStringExtra(RESULT_CHOOSE) != null) {
            val account = Gson().fromJson(data.getStringExtra(RESULT_CHOOSE), BankAccount::class.java)
            if (account != null) {
                viewModel.selectedAccount.postValue(account)
                longToast("You selected ${account.iban}")
                populateAccountLayout(account)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}