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
import io.stacrypt.stadroid.wallet.data.WalletRepository
import kotlinx.android.synthetic.main.fragment_transaction_detail.view.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.browse

class TransactionDetailViewModel : ViewModel() {

    val transactionId = MutableLiveData<Int>()

    val transaction = Transformations.switchMap(transactionId) { id ->
        id?.let { WalletRepository.getBankingTransactionById(it) }
    }
}

class TransactionDetailFragment : Fragment() {

    lateinit var viewModel: TransactionDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_transaction_detail, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(TransactionDetailViewModel::class.java)

        arguments?.getInt("id")?.let { viewModel.transactionId.postValue(it) }

        viewModel.transaction.observe(viewLifecycleOwner, Observer { transaction ->
            rootView.currency.text = transaction?.paymentGateway?.fiatSymbol
            rootView.currency_icon.setImageResource(transaction?.paymentGateway?.fiat?.iconResource()!!)
            rootView.type.text = when (transaction.type) {
                "cashin" -> "Fiat Deposit"
                "cashout" -> "Fiat Withdraw"
                else -> "Other"
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
            rootView.banking_id.text = when {
                transaction.type == "bank_card" -> transaction.bankingId?.pan?.panSecurityMask()!!
                transaction.type == "bank_account" -> transaction.bankingId?.iban?.ibanSecurityMask()!!
                else -> "Unknown"
            }

            rootView.status.text = when {
                (transaction.error != null) -> "Error: ${transaction.error}"
                (transaction.referenceId != null) -> "Finished"
                (transaction.transactionId == null) -> "In the way..."
                else -> "Waiting to be paid"
            }

            with(transaction) {
                if (type == "cashin" && error == null && referenceId == null && transactionId != null)
                    rootView.visibility = View.VISIBLE
                else
                    rootView.visibility = View.GONE
            }

            rootView.setOnClickListener {
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
                        browse("https://pay.ir/payment/gateway/${transaction.id}")
                    }

                }.show()
            }

        })

        return rootView
    }
}