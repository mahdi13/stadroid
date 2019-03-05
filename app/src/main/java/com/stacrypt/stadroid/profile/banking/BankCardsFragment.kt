package com.stacrypt.stadroid.profile.banking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.LinearLayoutManager

import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.BankCard
import kotlinx.android.synthetic.main.bank_cards_fragment.*
import kotlinx.android.synthetic.main.email_verification_fragment.view.*

class BankCardsFragment : Fragment() {

    private lateinit var viewModel: BankCardsViewModel
    private lateinit var adapter: BankCardPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bank_cards_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "My Cards"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BankCardsViewModel::class.java)

        adapter = BankCardPagedAdapter()
        list.adapter = adapter

        viewModel.bankCards.observe(this, Observer<PagedList<BankCard>> {
            adapter.submitList(it)
        })
        list.layoutManager = LinearLayoutManager(activity)

    }

}
