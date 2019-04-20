package io.stacrypt.stadroid.wallet.transactions

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
import io.stacrypt.stadroid.profile.banking.digestAddress
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.getCurrencyIconBySymbol
import io.stacrypt.stadroid.ui.ibanSecurityMask
import io.stacrypt.stadroid.ui.iconResource
import io.stacrypt.stadroid.ui.panSecurityMask
import io.stacrypt.stadroid.wallet.balance.BalanceDetailActivity
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.fragment_asset_balance.*
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.*
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.back
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.toast

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
        arguments?.getInt(ARG_ID)?.let {
            when (arguments?.getString(ARG_TYPE)) {
                TYPE_BANKING_TRANSATION -> viewModel.transactionId.postValue(it)
                TYPE_DEPOSIT -> viewModel.depositId.postValue(it)
                TYPE_WITHDRAW -> viewModel.withdrawId.postValue(it)
            }
        }

        viewModel.transaction.observe(viewLifecycleOwner, Observer { transaction ->
            if (transaction == null) return@Observer
            rootView.id_number.text = "# ${transaction.id}"
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

            rootView.confirmations.text = "---"

            rootView.pay.setOnClickListener {
                alert {
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

        viewModel.deposit.observe(viewLifecycleOwner, Observer { deposit ->
            if (deposit == null) return@Observer
            rootView.id_number.text = "# ${deposit.id}"
            rootView.currency.text = viewModel.symbol
            rootView.currency_icon.setImageResource(getCurrencyIconBySymbol(viewModel.symbol))

            rootView.amount.text = deposit.netAmount?.format10Digit()
                ?.plus(" ")
                ?.plus(viewModel.symbol)

            rootView.commission.text = (deposit.grossAmount - deposit.netAmount).abs().format10Digit()
                ?.plus(" ")
                ?.plus(viewModel.symbol)

            rootView.source.text = "---"
            rootView.dest.text = deposit.toAddress.address.digestAddress()

            rootView.created_at.text = deposit.invoice.creation?.format() ?: "NA"
            rootView.updated_at.text =
                deposit.invoice.expiration?.format() ?: deposit.invoice.creation?.format() ?: "NA"
            rootView.payment_gateway.text = "---"

            rootView.payment_id.text = "---"
            rootView.transaction_id.text = deposit.txHash?.digestAddress() ?: "NA"
            rootView.reference_id.text = deposit.txHash?.digestAddress() ?: "NA"

            rootView.pay.text = "Show More..."
            rootView.pay.setOnClickListener {
                if (deposit.link != null) browse(deposit.link!!)
                else toast("Not Available")
            }

            rootView.status.text = deposit.status
            rootView.confirmations.text = if (deposit.confirmationsRequires != null)
                "${deposit.confirmationsRequires!! - (deposit.confirmationsLeft
                    ?: deposit.confirmationsRequires!!)} of ${deposit.confirmationsRequires}"
            else
                "NA"

            rootView.type.text = "Cryptocurrency Deposit"
        })

        viewModel.withdraw.observe(viewLifecycleOwner, Observer { withdraw ->
            if (withdraw == null) return@Observer
            rootView.id_number.text = "# ${withdraw.id}"
            rootView.currency.text = viewModel.symbol
            rootView.currency_icon.setImageResource(getCurrencyIconBySymbol(viewModel.symbol))

            rootView.amount.text = withdraw.netAmount?.format10Digit()
                ?.plus(" ")
                ?.plus(viewModel.symbol)

            rootView.commission.text = (withdraw.grossAmount - withdraw.netAmount).abs().format10Digit()
                ?.plus(" ")
                ?.plus(viewModel.symbol)

            rootView.source.text = "---"
            rootView.dest.text = withdraw.toAddress.toString().digestAddress()

            rootView.created_at.text = withdraw.issuedAt?.format() ?: "NA"
            rootView.updated_at.text = withdraw.paidAt?.format() ?: "NA"
            rootView.payment_gateway.text = "---"

            rootView.payment_id.text = "---"
            rootView.transaction_id.text = withdraw.txid?.digestAddress() ?: "NA"
            rootView.reference_id.text = withdraw.txid?.digestAddress() ?: "NA"

            rootView.pay.text = "Show More..."
            rootView.pay.setOnClickListener {
                if (withdraw.txid != null) browse(withdraw.link!!)
                else toast("Not Available")
            }

            rootView.status.text = withdraw.status
            rootView.confirmations.text = if (withdraw.confirmationsRequires != null)
                "${withdraw.confirmationsRequires!! - (withdraw.confirmationsLeft
                    ?: withdraw.confirmationsRequires!!)} of ${withdraw.confirmationsRequires}"
            else
                "NA"

            rootView.type.text = "Cryptocurrency Withdraw"
        })


        rootView.back.setOnClickListener { (activity as BalanceDetailActivity).up() }

        return rootView
    }

    companion object {
        val ARG_SYMBOL = "symbol"
        val ARG_ID = "id"
        val ARG_TYPE = "type"
        val TYPE_BANKING_TRANSATION = "transaction"
        val TYPE_WITHDRAW = "withdraw"
        val TYPE_DEPOSIT = "deposit"
    }
}