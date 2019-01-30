package com.stacrypt.stadroid.market

import android.graphics.Color
import android.graphics.Paint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.fragment_market.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.XAxis
import com.google.android.material.tabs.TabLayout


/**
 * A placeholder fragment containing a simple view.
 */
class MarketActivityFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) = viewpager.setCurrentItem(tab!!.position)
        })
        viewpager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> MarketCandlestickFragment.newInstance("", "")
                    1 -> MarketHistoryFragment.newInstance(1)
                    2 -> MarketMineFragment.newInstance(1)
                    else -> MarketMineFragment.newInstance(1)
                }
            }

            override fun getCount(): Int = 2

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_market, container, false)
    }
}
