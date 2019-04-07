package io.stacrypt.stadroid.profile.banking

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.BankAccount
import io.stacrypt.stadroid.profile.BaseSettingFragment
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import kotlinx.android.synthetic.main.bank_accounts_fragment.view.*
import kotlinx.android.synthetic.main.header_appbar_back.view.*

class BankAccountsFragment : BaseSettingFragment() {

    private lateinit var viewModel: BankAccountsViewModel
    private lateinit var adapter: BankAccountPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.bank_accounts_fragment, container, false)

        viewModel = ViewModelProviders.of(activity!!).get(BankAccountsViewModel::class.java)

        adapter = BankAccountPagedAdapter()
        rootView.list.adapter = adapter

        viewModel.bankAccounts.observe(this, Observer<PagedList<BankAccount>> {
            if (it.size > 0) rootView.no_result.visibility = View.GONE
            adapter.submitList(it)
        })
        rootView.list.layoutManager = LinearLayoutManager(activity)

        rootView.add.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_bankAccountsFragment_to_addBankAccountFragment, Bundle().apply {
                    putString(ProfileSettingActivity.ARG_LAUNCH_MODE, ProfileSettingActivity.LAUNCH_MODE_NORMAL)
                })
        }

        if (arguments?.getString(ProfileSettingActivity.ARG_ACTION) == ProfileSettingActivity.ACTION_CHOOSE) {
            adapter.onItemClickListener = {
                activity?.setResult(
                    Activity.RESULT_OK,
                    Intent().apply { putExtra(ProfileSettingActivity.RESULT_CHOOSE, Gson().toJson(it)) })
                if (arguments?.getString(ProfileSettingActivity.ARG_LAUNCH_MODE) == ProfileSettingActivity.LAUNCH_MODE_DIALOG) back()
            }
        }


        return rootView
    }
}
