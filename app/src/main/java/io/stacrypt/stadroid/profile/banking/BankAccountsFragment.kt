package io.stacrypt.stadroid.profile.banking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView

import io.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.header_appbar_back.view.*

class BankAccountsFragment : Fragment() {
    private lateinit var viewModel: BankAccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bank_accounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.appbar.setOnClickListener {
            NavHostFragment.findNavController(this@BankAccountsFragment).navigateUp()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BankAccountsViewModel::class.java)

//        view?.findViewById<RecyclerView>(R.id.list)?.adapter = viewModel?.bankAccountListing?.value
    }

}
