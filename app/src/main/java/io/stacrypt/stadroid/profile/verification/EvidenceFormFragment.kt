package io.stacrypt.stadroid.profile.verification

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.stacrypt.stadroid.R

class EvidenceFormFragment : Fragment() {

    private lateinit var viewModel: EvidenceFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.evidence_form_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EvidenceFormViewModel::class.java)
//        val mainClassNamebinding: UserBinding = DataBindingUtil.setContentView(this, R.layout.user)
//        binding.viewmodel = userModel


    }

}
