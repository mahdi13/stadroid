package com.stacrypt.stadroid.profile.banking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stacrypt.stadroid.R

class BankAccountsFragment : Fragment() {

    companion object {
        fun newInstance() = BankAccountsFragment()
    }

    private lateinit var viewModel: BankAccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bank_accounts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BankAccountsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
