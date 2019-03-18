package io.stacrypt.stadroid.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.User
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_APPLICATION_PIN
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_CHANEG_PASSWORD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_VERIFICATION_PROCESS
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.jetbrains.anko.support.v4.startActivity

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private var listener: OnProfileInteractionListener? = null

    private fun View.setTarget(target: String) = setOnClickListener {
        startActivity<ProfileSettingActivity>(ARG_TARGET to target)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false).apply {

        logout.setOnClickListener {
            sessionManager.logout()
            // TODO: Clear entire db
            listener?.onLoggedOut()
        }

        verification.setTarget(TARGET_VERIFICATION_PROCESS)
        bank_cards.setTarget(TARGET_BANK_CARDS)
        pin.setTarget(TARGET_APPLICATION_PIN)
        change_password.setTarget(TARGET_CHANEG_PASSWORD)
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
        viewModel = ViewModelProviders.of(activity!!).get(ProfileViewModel::class.java)
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