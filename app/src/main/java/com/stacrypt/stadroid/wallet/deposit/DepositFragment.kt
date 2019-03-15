package com.stacrypt.stadroid.wallet.deposit

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.wallet.ui.balancedetail.BalanceDetailViewModel
import kotlinx.android.synthetic.main.deposit_fragment.view.*


class DepositFragment : Fragment() {

    private lateinit var viewModel: BalanceDetailViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: Refresh deposit info
        viewModel.depositInfo.observe(this, Observer {
            view.address_view.text = it?.address

            if (it?.extra == null || it.extra!!.isEmpty()) {
                view.tag_container.visibility = View.GONE
            } else {
                view.tag_container.visibility = View.VISIBLE
                view.tag_view.text = it.extra
            }
        })

        viewModel.currency.observe(this, Observer {
            view.min_limit.text = it.depositMin?.toString() ?: "No limit"
            view.max_limit.text = it.depositMax?.toString() ?: "No limit"
            view.fee.text = "${(it.depositStaticCommission) ?: 0} + ${(it.depositPermilleCommission ?: 0) / 10f}%"
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BalanceDetailViewModel::class.java)

    }
}
