package io.stacrypt.stadroid.wallet.deposit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialDialogs
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.copyToClipboard
import io.stacrypt.stadroid.ui.showQrCode
import io.stacrypt.stadroid.wallet.BalanceDetailActivity.Companion.ARG_ASSET
import kotlinx.android.synthetic.main.deposit_fragment.view.*
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.design.snackbar


class DepositFragment : Fragment() {

    private lateinit var viewModel: DepositViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.deposit_fragment, container, false)


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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DepositViewModel::class.java)
        viewModel.init(arguments?.getString(ARG_ASSET)!!)
        populateUi()
    }

    private fun populateUi() {
        viewModel.depositInfo.observe(this, Observer {
            view?.address_view?.text = it?.address

            if (it?.address != null) {
//                view?.tag_qrcode_image?.showQrCode(it.address)
                view?.address_qrcode_image?.setImageBitmap(
                    QRCode.from(it.address).bitmap()
                )
            }

            val renewButton: Button?
            if (it?.extra == null || it.extra!!.isEmpty()) {
                view?.tag_container?.visibility = View.GONE
                view?.address_renew?.visibility = View.VISIBLE
                renewButton = view?.address_renew
            } else {
                view?.tag_container?.visibility = View.VISIBLE
                view?.address_renew?.visibility = View.GONE
                view?.tag_view?.text = it.extra
                view?.tag_qrcode_image?.showQrCode(it.extra!!)
                renewButton = view?.tag_renew
            }

            renewButton?.setOnClickListener {
                MaterialDialog(activity!!).show {
                    title(R.string.are_you_sure)
                    message(text = "If you renew your address, your previous address will be invalidated and could not be used anymore.")
                    positiveButton(text = "Renew") {
                        val prevAddress = viewModel.depositInfo.value?.address ?: view?.address_view?.text.toString()
                        view?.address_view?.text = getString(R.string.loading)
                        viewModel.renewDepositInfo {
                            if (prevAddress != null) view?.address_view?.text = prevAddress
                            view?.snackbar("You should use this address at least once before you can renew it")
                        }
                    }
                    negativeButton(R.string.cancel)
                }
            }

        })

        viewModel.currency.observe(this, Observer {
            if (it == null) return@Observer
            view?.min_limit?.text = it.depositMin?.toString() ?: "No limit"
            view?.max_limit?.text = it.depositMax?.toString() ?: "No limit"
            view?.fee?.text = "${(it.depositStaticCommission) ?: 0} + ${(it.depositPermilleCommission ?: 0) / 10f}%"
        })

        view?.address_copy?.setOnClickListener {
            activity?.copyToClipboard("address", view?.address_view?.text.toString())
        }

        view?.tag_copy?.setOnClickListener {
            activity?.copyToClipboard("tag", view?.tag_view?.text.toString())
        }


    }
}
