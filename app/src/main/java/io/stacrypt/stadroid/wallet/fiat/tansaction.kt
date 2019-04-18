package io.stacrypt.stadroid.wallet.fiat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.format
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.ibanSecurityMask
import io.stacrypt.stadroid.ui.iconResource
import io.stacrypt.stadroid.ui.panSecurityMask
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.*
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.back
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.browse

class TransactionDetailViewModel : ViewModel() {

    /**
     * Just one of the following items will be filled
     */
    lateinit var symbol: String

    val transactionId = MutableLiveData<Int>()
    val depositId = MutableLiveData<Int>()
    val withdrawId = MutableLiveData<Int>()

    val transaction = Transformations.switchMap(transactionId) { id ->
        id?.let { WalletRepository.getBankingTransactionById(it) }
    }

    val deposit = Transformations.switchMap(depositId) { id ->
        id?.let { WalletRepository.getDepositDetail(symbol, it) }
    }

    val withdraw = Transformations.switchMap(withdrawId) { id ->
        id?.let { WalletRepository.getWithdrawDetail(symbol, it) }
    }
}

class TransactionDetailFragment : Fragment() {

    lateinit var viewModel: TransactionDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_transaction_detail, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(TransactionDetailViewModel::class.java)

        arguments?.getString(ARG_SYMBOL)?.let { viewModel.symbol = it }
        arguments?.getInt(ARG_TRANSACTION_ID)?.let { viewModel.transactionId.postValue(it) }
        arguments?.getInt(ARG_DEPOSIT_ID)?.let { viewModel.depositId.postValue(it) }
        arguments?.getInt(ARG_WITHDRAW_ID)?.let { viewModel.withdrawId.postValue(it) }

        viewModel.transaction.observe(viewLifecycleOwner, Observer { transaction ->
            if (transaction == null) return@Observer
            rootView.currency.text = transaction?.paymentGateway?.fiatSymbol
            rootView.currency_icon.setImageResource(transaction?.paymentGateway?.fiat?.iconResource()!!)

            when (transaction.type) {
                "cashin" -> {
                    rootView.type.text = "Fiat Deposit"

                    if (transaction.referenceId == null && transaction.paymentId != null) {
                        rootView.pay.visibility = View.VISIBLE
                    } else {
                        rootView.pay.visibility = View.GONE
                    }
                }
                "cashout" -> {
                    rootView.type.text = "Fiat Withdraw"
                }
                else -> {
                    rootView.type.text = "Other"
                }
            }

            rootView.amount.text = transaction.amount?.format10Digit()
                ?.plus(" ")
                ?.plus(transaction.paymentGateway.fiatSymbol)

            rootView.commission.text = transaction.commission?.format10Digit()
                ?.plus(" ")
                ?.plus(transaction.paymentGateway.fiatSymbol)

            rootView.transaction_id.text = transaction.transactionId ?: "NA"
            rootView.reference_id.text = transaction.referenceId ?: "NA"
            rootView.created_at.text = transaction.createdAt?.format() ?: "NA"
            rootView.updated_at.text = transaction.modifiedAt?.format() ?: "NA"
            rootView.payment_gateway.text = transaction.paymentGatewayName
            rootView.source.text = when {
                transaction.bankingId?.type == "bank_card" -> transaction.bankingId?.pan?.panSecurityMask()!!
                transaction.bankingId?.type == "bank_account" -> transaction.bankingId?.iban?.ibanSecurityMask()!!
                else -> "Unknown"
            }

            rootView.status.text = when {
                (transaction.error != null) -> "Error: ${transaction.error}"
                (transaction.referenceId != null) -> "Finished"
                (transaction.transactionId == null) -> "In the way..."
                else -> "Waiting to be paid"
            }

            rootView.pay.setOnClickListener {
                alert {
                    ctx.setTheme(R.style.AlertDialogCustom)
                    title = "You are going to pay..."
                    message = """
                       You will be redirected to the payment page,
                       and for security reasons it should be done
                       in your device's browser. Please BE CAREFUL
                       about everything, use a trusted browser,
                       be sure that the payment url is exactly
                       matched with "shaparak.ir/" and make sure
                       that the page is loading under "https" protocol.
                    """.trimIndent()

                    positiveButton("Let's go") {
                        browse("https://pay.ir/pg/${transaction.paymentId}")
                    }

                }.show()
            }

        })

        rootView.back.setOnClickListener { (activity as BalanceDetailActivity).up() }

        return rootView
    }

    companion object {
        val ARG_SYMBOL = "transaction_id"
        val ARG_TRANSACTION_ID = "transaction_id"
        val ARG_DEPOSIT_ID = "deposit_id"
        val ARG_WITHDRAW_ID = "withdraw_id"
    }
}