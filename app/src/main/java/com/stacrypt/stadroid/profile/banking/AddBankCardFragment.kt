package com.stacrypt.stadroid.profile.banking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stacrypt.stadroid.R

class AddBankCardFragment : Fragment() {

    companion object {
        fun newInstance() = AddBankCardFragment()
    }

    private lateinit var viewModel: AddBankCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_bank_card_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddBankCardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
