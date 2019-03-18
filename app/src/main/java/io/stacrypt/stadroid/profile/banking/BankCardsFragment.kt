package io.stacrypt.stadroid.profile.banking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankCard
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.bank_cards_fragment.*

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
        activity!!.add.isVisible = true
        activity!!.back.isVisible = true
        activity!!.add.setOnClickListener {
            NavHostFragment.findNavController(this@BankCardsFragment).navigate(R.id.addBankCardFragment)
        }
        activity!!.back.setOnClickListener {
            NavHostFragment.findNavController(this@BankCardsFragment).navigateUp()
        }
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
