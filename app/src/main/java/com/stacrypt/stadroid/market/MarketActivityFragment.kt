package com.stacrypt.stadroid.market

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.fragment_market.*
import com.google.android.material.tabs.TabLayout
import java.lang.Exception
import java.lang.IndexOutOfBoundsException


/**
 * A placeholder fragment containing a simple view.
 */
class MarketActivityFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab!!.position
            }
        })
        viewpager.offscreenPageLimit = 8
        viewpager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> MarketMineFragment.newInstance(1)
                    1 -> MarketBookFragment.newInstance(2)
                    2 -> MarketCandlestickFragment.newInstance("", "")
                    3 -> MarketHistoryFragment.newInstance(1)
                    else -> throw IndexOutOfBoundsException()
                }
            }

            override fun getCount(): Int = 4
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_market, container, false)
    }
}
