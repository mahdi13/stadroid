package com.stacrypt.stadroid.wallet.ui.balancedetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stacrypt.stadroid.R

class BalanceDetailFragment : Fragment() {

    companion object {
        fun newInstance() = BalanceDetailFragment()
    }

    private lateinit var viewModel: BalanceDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.balance_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BalanceDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
