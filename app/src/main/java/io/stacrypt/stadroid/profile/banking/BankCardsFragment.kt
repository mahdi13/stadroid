package io.stacrypt.stadroid.profile.banking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankCard
import io.stacrypt.stadroid.profile.BaseSettingFragment
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import kotlinx.android.synthetic.main.bank_cards_fragment.view.*

class BankCardsFragment : BaseSettingFragment() {

    private lateinit var viewModel: BankCardsViewModel
    private lateinit var adapter: BankCardPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.bank_cards_fragment, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(BankCardsViewModel::class.java)

        adapter = BankCardPagedAdapter()
        rootView.list.adapter = adapter

        viewModel.bankCards.observe(this, Observer<PagedList<BankCard>> {
            if (it.size > 0) rootView.no_result.visibility = View.GONE
            adapter.submitList(it)
        })
        rootView.list.layoutManager = LinearLayoutManager(activity)

        rootView.add.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_bankCardsFragment_to_addBankCardFragment, Bundle().apply {
                    putString(ProfileSettingActivity.ARG_LAUNCH_MODE, ProfileSettingActivity.LAUNCH_MODE_NORMAL)
                })
        }

        return rootView
    }
}
