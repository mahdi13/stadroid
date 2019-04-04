package io.stacrypt.stadroid.market.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.market.MarketViewModel
import io.stacrypt.stadroid.ui.format10Digit
import io.stacrypt.stadroid.ui.iconResource
import kotlinx.android.synthetic.main.fragment_market_info.*
import org.jetbrains.anko.imageResource

class MarketInfoFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_market_info, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)

        viewModel.market.observe(viewLifecycleOwner, Observer {
            market_name.text = it?.name?.replace("_", " / ")
        })

        viewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
            base_currency.text = it?.symbol
            base_currency_icon.imageResource = it.iconResource()
        })

        viewModel.quoteCurrency.observe(viewLifecycleOwner, Observer {
            quote_currency.text = it?.symbol
            quote_currency_icon.imageResource = it.iconResource()
        })

        viewModel.summary.observe(viewLifecycleOwner, Observer {
            ask_amount.text = it?.askAmount?.format10Digit()
            bid_amount.text = it?.bidAmount?.format10Digit()
            asks.text = it?.askCount.toString()
            bids.text = it?.bidCount.toString()
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            open.text = it?.open?.format10Digit()
            highest.text = it?.high?.format10Digit()
            lowest.text = it?.low?.format10Digit()
            close.text = it?.close?.format10Digit()
            volume.text = it?.volume?.format10Digit()
            deal.text = it?.deal?.format10Digit()
            market_cap.text = it?.volume?.format10Digit() // FIXME
        })

        return rootView
    }


}