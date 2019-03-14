package com.stacrypt.stadroid.profile.verification

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.verification_process_fragment.*

class VerificationProcessFragment : Fragment() {

    private lateinit var viewModel: VerificationProcessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.verification_process_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(VerificationProcessViewModel::class.java)

        viewModel.client.observe(this, Observer {
            if (!it.isEmailVerified) {
                // Step 1
                stepper.currentStep = 1
                childFragmentManager.beginTransaction().
            }else{
                stepper.currentStep = 1
            }
        })
    }

}
