package io.stacrypt.stadroid.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.data.Market
import kotlinx.android.synthetic.main.fragment_market_summary.view.*
import kotlinx.android.synthetic.main.market_summary_ohlc.view.*
import kotlinx.android.synthetic.main.market_summary_price.view.*
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.ui.format
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.coroutines.*
import org.jetbrains.anko.textColorResource
import java.math.BigDecimal
import java.math.RoundingMode


const val SHOWING_ITEMS = 20

class MarketSummaryFragment : Fragment() {
    private lateinit var viewModel: MarketViewModel
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_market_summary, container, false)!!
        rootView?.back?.setOnClickListener { activity?.finish() }
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        viewModel.market.observe(this, Observer<Market?> { market ->
            if (market == null) return@Observer
            rootView?.market_name?.text = market.name.replace("_", " / ")
        })

        viewModel.allMarkets.observe(this, Observer<List<Market>> { markets ->
        })

        viewModel.status.observe(this, Observer {
            rootView?.open?.text = it?.open?.format10Digit()
            rootView?.high?.text = it?.high?.format10Digit()
            rootView?.low?.text = it?.low?.format10Digit()
            rootView?.close?.text = it?.close?.format10Digit()
            rootView?.volume?.text =
                it?.volume?.format10Digit() + " " + (viewModel?.marketName?.value?.split("_")?.get(1) ?: "")

            if (viewModel.last.value?.price != null && it?.volume != null) {
                rootView?.volume_value?.text =
                    (it?.volume * viewModel.last.value?.price!!).format10Digit() + " " + (viewModel?.marketName?.value?.split(
                        "_"
                    )?.get(0) ?: "")
            }

            if (it.open != BigDecimal(0)) {
                val precentagePrefix: String
                val precentageChange: String
                if (it.open > it.close) {
                    rootView?.deal?.textColorResource = R.color.real_red_semi_trans
                    precentagePrefix = "-"
                    precentageChange =
                        (it.open - it.close).divide(it.open, 8, RoundingMode.HALF_EVEN).scaleByPowerOfTen(2).format(2)
                } else {
                    rootView?.deal?.textColorResource = R.color.real_green_semi_trans
                    precentagePrefix = "+"
                    precentageChange =
                        (it.close - it.open).divide(it.open, 8, RoundingMode.HALF_EVEN).scaleByPowerOfTen(2).format(2)
                }
                rootView?.deal?.text = "$precentagePrefix $precentageChange %"
            }
        })

        viewModel.last.observe(this, Observer {
            rootView?.price?.text = "Last: ${it?.price?.format10Digit()}"

            if (viewModel.status.value?.volume != null && it?.price != null) {
                rootView?.volume_value?.text =
                    (it?.price * viewModel.status.value?.volume!!).format10Digit() + " " + (viewModel?.marketName?.value?.split(
                        "_"
                    )?.get(0) ?: "")
            }

        })

        viewModel.status.observe(this, Observer {
            //TODO
        })

        GlobalScope.launch(Dispatchers.Main) {
            while (this.isActive) {
                view?.flip?.flipTheView(); delay(3000)
            }
        }

    }


}
