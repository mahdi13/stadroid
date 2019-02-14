package com.stacrypt.stadroid.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.User

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private var listener: OnProfileInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProfileInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnProfileInteractionListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.user?.observe(this,
            Observer<User> { user ->

            })

    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnProfileInteractionListener {
        fun onLoggedOut()
    }

}
