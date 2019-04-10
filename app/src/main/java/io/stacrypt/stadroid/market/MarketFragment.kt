package io.stacrypt.stadroid.market

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import io.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.fragment_market.*
import com.google.android.material.tabs.TabLayout
import io.stacrypt.stadroid.market.book.MarketBookFragment
import io.stacrypt.stadroid.market.chart.MarketCandlestickFragment
import io.stacrypt.stadroid.market.info.MarketInfoFragment
import io.stacrypt.stadroid.market.marketdeals.MarketDealsFragment
import io.stacrypt.stadroid.market.mydeals.MarketMyDealsFragment
import io.stacrypt.stadroid.market.myorders.MyOrdersFragment
import java.lang.IndexOutOfBoundsException

/**
 * A placeholder fragment containing a simple view.
 */
class MarketFragment : Fragment() {

    private lateinit var viewModel: MarketViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab!!.position
            }
        })
        viewpager.offscreenPageLimit = 12
        viewpager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> MarketCandlestickFragment.newInstance("", "")
                    1 -> MyOrdersFragment()
                    2 -> MarketBookFragment()
                    3 -> MarketMyDealsFragment()
                    4 -> MarketDealsFragment()
                    5 -> MarketInfoFragment()
                    else -> throw IndexOutOfBoundsException()
                }
            }

            override fun getCount(): Int = 6
        }
//        tabs.setupWithViewPager(viewpager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(MarketViewModel::class.java)
        arguments?.getString(MarketActivity.ARG_MARKET)?.let { viewModel.marketName = it }
        return inflater.inflate(R.layout.fragment_market, container, false)
    }
}
