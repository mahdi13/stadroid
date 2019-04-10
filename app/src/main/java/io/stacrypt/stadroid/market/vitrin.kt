package io.stacrypt.stadroid.market

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.*
import io.stacrypt.stadroid.market.data.MarketRepository
import kotlinx.android.synthetic.main.fragment_market_vitrine.*
import kotlinx.android.synthetic.main.fragment_market_vitrine.view.*
import org.jetbrains.anko.support.v4.withArguments
import androidx.core.content.ContextCompat
import io.stacrypt.stadroid.ui.format10Digit
import kotlinx.android.synthetic.main.fragment_market_vitrine_list.*
import org.jetbrains.anko.support.v4.startActivity

class MarketVitrineViewModel : ViewModel() {
    val allMarkets: LiveData<List<Market>> = MarketRepository.getMarkets()
}

class MarketVitrineFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_market_vitrine_list, container, false)
        viewModel = ViewModelProviders.of(this).get(MarketVitrineViewModel::class.java)

        viewModel.allMarkets.observe(viewLifecycleOwner, Observer<List<Market>> { markets ->
            markets.forEach { market ->
                // FIXME
                when (market.name) {
                    "TIRR_TBTC" -> {
                        container1.setOnClickListener {
                            startActivity<MarketActivity>(MarketActivity.ARG_MARKET to market.name)
                        }
                        childFragmentManager.beginTransaction()
                            .replace(R.id.container1, MarketVitrineRowFragment().withArguments("market" to market.name))
                            .commitNow()
                    }
                   "TIRR_TETH" -> {
                       childFragmentManager.beginTransaction()
                           .replace(R.id.container2, MarketVitrineRowFragment().withArguments("market" to market.name))
                           .commitNow()
                   }
                   "TBTC_TETH" -> {
                       childFragmentManager.beginTransaction()
                           .replace(R.id.container3, MarketVitrineRowFragment().withArguments("market" to market.name))
                           .commitNow()
                   }
                }
            }
        })
        return rootView
    }

    private lateinit var viewModel: MarketVitrineViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}

class VitrineRowViewModel : ViewModel() {

    init {
        Log.e("salammmsalammm", "New Live Data")
    }

    lateinit var marketName: String

    val market: LiveData<Market> by lazy { MarketRepository.getMarket(marketName) }

    val last: LiveData<MarketLast> by lazy { MarketRepository.getMarketLast(marketName) }

    val summary: LiveData<MarketSummary> by lazy { MarketRepository.getMarketSummary(marketName) }

    val status: LiveData<MarketStatus> by lazy { MarketRepository.getMarketStatus(marketName) }

    val kline: LiveData<List<Kline>> by lazy { MarketRepository.getKline24(marketName) }

    override fun onCleared() {
        // (kline as PullerLiveData<*>).destroy()
        Log.e("salammmsalammm", "Live Data Dead :(((")
        super.onCleared()
    }
}

class MarketVitrineRowFragment : Fragment() {
    private lateinit var viewModel: VitrineRowViewModel
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_market_vitrine, container, false)!!


        viewModel = ViewModelProviders.of(this).get(VitrineRowViewModel::class.java)
        arguments?.getString("market")?.let {
            viewModel.marketName = it
        }

        viewModel.market.observe(viewLifecycleOwner, Observer<Market?> { market ->
            if (market == null) return@Observer
            rootView?.market_name?.text = market.name.replace("_", " / ")
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            //            rootView?.open?.text = it?.low?.format10Digit()
            rootView?.high?.text = it?.high?.format10Digit()
            rootView?.low?.text = it?.low?.format10Digit()
//            rootView?.close?.text = it?.last?.format10Digit()
            rootView?.volume?.text =
                "vol " + it?.volume?.format10Digit() // + " " + (viewModel?.marketName?.value?.split("_")?.get(1) ?: "")
        })

        viewModel.last.observe(viewLifecycleOwner, Observer {
            rootView?.price?.text = it?.price.toString()
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            // TODO
        })

        viewModel.kline.observe(viewLifecycleOwner, Observer {
            initChart(initDataset(it))
        })


        return rootView
    }

    private fun initDataset(items: List<Kline>?): LineDataSet? {
        val values = ArrayList<Entry>()
        items?.forEachIndexed { i, it ->
            values.add(
                Entry(
                    i.toFloat(), (it.l + it.h).toFloat() / 2f
                )
            )
        }
        if (values.isNullOrEmpty()) return null
        val dataset = LineDataSet(values, "")
        dataset.setDrawIcons(false)
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataset.cubicIntensity = 0.2f
        dataset.colors = listOf(resources.getColor(R.color.colorSecondary))
        dataset.valueTextSize = 0f
        dataset.fillAlpha = 200
        dataset.setDrawFilled(true)
        dataset.setDrawCircleHole(false)
        dataset.setDrawCircles(false)
//        dataset.fillColor = resources.getColor(R.color.colorSecondary)
        dataset.lineWidth = 3f
        dataset.fillDrawable = ContextCompat.getDrawable(context!!, R.drawable.mini_chart_fill)
        dataset.fillFormatter = IFillFormatter { _, _ ->
            // change the return value here to better understand the effect
            // return 0;
            mini_chart.axisLeft.axisMinimum
        }

        return dataset
    }

    private fun initChart(dataset: LineDataSet?) {
        // Set dataset
        if (dataset != null) {
            mini_chart.data = LineData(dataset)
            mini_chart.notifyDataSetChanged()
            //        mini_chart.setVisibleYRangeMinimum(1_000F, YAxis.AxisDependency.RIGHT)
//        mini_chart.setVisibleYRangeMaximum(10_000F, YAxis.AxisDependency.RIGHT)
//        mini_chart.setVisibleYRangeMinimum(1_000F, YAxis.AxisDependency.LEFT)
//        mini_chart.setVisibleYRangeMaximum(10_000F, YAxis.AxisDependency.LEFT)
            mini_chart.invalidate()
//        mini_chart.setMaxVisibleValueCount(100)
//        mini_chart.zoom(1F, 1F, 100F, 100F)
            // scaling can now only be done on x- and y-axis separately
            mini_chart.isDoubleTapToZoomEnabled = false
            mini_chart.isNestedScrollingEnabled = false
            mini_chart.isHorizontalScrollBarEnabled = false
            mini_chart.isVerticalScrollBarEnabled = false
            mini_chart.setVisibleXRange(SHOWING_ITEMS.toFloat(), SHOWING_ITEMS.toFloat())
            mini_chart.moveViewToX((dataset.values.size - SHOWING_ITEMS / 2).toFloat())
            mini_chart.description.isEnabled = false
        }

        // Colors
        mini_chart.setBackgroundColor(resources.getColor(R.color.colorPrimary))

        // Touch
        mini_chart.requestDisallowInterceptTouchEvent(false)
        mini_chart.axisRight.isEnabled = false
        mini_chart.axisLeft.isEnabled = false
        mini_chart.xAxis.isEnabled = false
        mini_chart.isHighlightPerDragEnabled = false

        mini_chart.setDrawBorders(false)
        mini_chart.setBorderWidth(0F)
//        mini_chart.setBorderColor(resources.getColor(R.color.colorLightGray))
        mini_chart.legend.isEnabled = false
        mini_chart.isFocusableInTouchMode = false
        mini_chart.setTouchEnabled(false)
        mini_chart.isDragEnabled = false
        mini_chart.setScaleEnabled(false)
        mini_chart.setPinchZoom(false)
        mini_chart.isScaleYEnabled = false
        mini_chart.isScaleXEnabled = false
        mini_chart.setNoDataText("")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}

// class MarketVitrineRowUi : AnkoComponent<ViewGroup> {
//    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
//        verticalLayout {
//
//        }
//    }
// }

// class MarketVitrineViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
// //    val name: TextView = mView.
//
//    fun bind(market: Market) {
//
//    }
// }

// class MarketVitrineAdapter(
//    var items: List<Market>,
//    val fragmentManager: FragmentManager,
//    val onMarketSelectedListener: (String) -> Unit
// ) : BaseAdapter() {
//    @SuppressLint("ViewHolder")
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//
//        fragmentManager.beginTransaction().replace()
//        val rootView = View.inflate(convertView!!.context, R.layout.fragment_market_vitrine, parent)
//
//        return rootView
//    }
//
//    override fun getItem(position: Int): Any = items[position]
//
//    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()
//
//    override fun getCount(): Int = items.size
//
// }
