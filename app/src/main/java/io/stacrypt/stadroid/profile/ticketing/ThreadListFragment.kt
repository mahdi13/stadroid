package io.stacrypt.stadroid.profile.ticketing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.Listing
import io.stacrypt.stadroid.data.Ticket
import io.stacrypt.stadroid.data.TicketListDataSourceFactory
import io.stacrypt.stadroid.data.listing
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.profile.BaseSettingFragment
import kotlinx.android.synthetic.main.fragment_ticket_list.view.*

class TicketListViewModel : ViewModel() {

    private val ticketsListing by lazy { TicketListDataSourceFactory().listing() }

    val tickets by lazy { ticketsListing.pagedList }
}

class TicketListFragment : BaseSettingFragment() {

    lateinit var viewModel: TicketListViewModel

    override fun authorize() = sessionManager.isSemiTrustedClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_ticket_list, container, false)

        viewModel = ViewModelProviders.of(this).get(TicketListViewModel::class.java)

        viewModel.tickets.observe(viewLifecycleOwner, Observer {

        })

        rootView.list.adapter =

        return rootView
    }
}