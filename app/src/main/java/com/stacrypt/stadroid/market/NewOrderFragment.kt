package com.stacrypt.stadroid.market

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stacrypt.stadroid.R

class NewOrderFragment : Fragment() {

    companion object {
        fun newInstance() = NewOrderFragment()
    }

    private lateinit var viewModel: NewOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_order_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(NewOrderViewModel::class.java)
    }

}
