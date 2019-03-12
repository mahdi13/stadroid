package com.stacrypt.stadroid.wallet.deposit

import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.zxing.BarcodeFormat

import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.wallet.ui.balancedetail.BalanceDetailViewModel
import android.widget.ImageView
import com.google.zxing.WriterException
import net.glxn.qrgen.android.QRCode


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


    }
}
