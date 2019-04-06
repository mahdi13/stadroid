package io.stacrypt.stadroid.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.User
import io.stacrypt.stadroid.data.format
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_APPLICATION_PIN
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_ACCOUNTS
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_CHANEG_PASSWORD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_VERIFICATION_PROCESS
import io.stacrypt.stadroid.profile.banking.extractIpAddress
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColorResource

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private var listener: OnProfileInteractionListener? = null

    private fun View.setTarget(target: String) = setOnClickListener {
        startActivity<ProfileSettingActivity>(ARG_TARGET to target)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false).apply {

        logout.setOnClickListener {
            sessionManager.logout()
            // TODO: Call logout service
            // TODO: Terminate current session
            // TODO: Clear entire db
            listener?.onLoggedOut()
        }

        verification.setTarget(TARGET_VERIFICATION_PROCESS)
        bank_cards.setTarget(TARGET_BANK_CARDS)
        bank_accounts.setTarget(TARGET_BANK_ACCOUNTS)
        pin.setTarget(TARGET_APPLICATION_PIN)
        change_password.setTarget(TARGET_CHANEG_PASSWORD)

        viewModel = ViewModelProviders.of(activity!!).get(ProfileViewModel::class.java)
        viewModel.user?.observe(viewLifecycleOwner,
            Observer<User> { user ->
                this.email.text = user.email
            })

        viewModel.lastLogin.observe(viewLifecycleOwner,
            Observer { lastLogin ->
                this.last_login.text = StringBuilder().append("Last login on ").append(
                    if (lastLogin?.createdAt != null) lastLogin.createdAt?.format()
                    else "Unknown"
                )
                    .append(" By ")
                    .append(lastLogin?.details?.extractIpAddress() ?: "Unknown")
            })


        when {
            sessionManager.isTrustedClient -> {
                rootView.verification_status.text = "Verified"
                rootView.verification_status.textColorResource = R.color.real_green
            }
            sessionManager.isSemiTrustedClient -> {
                rootView.verification_status.text = "Partially Verified"
                rootView.verification_status.textColorResource = R.color.yellow
            }
            sessionManager.isClient -> {
                rootView.verification_status.text = "Unverified"
                rootView.verification_status.textColorResource = R.color.real_red
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProfileInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnProfileInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnProfileInteractionListener {
        fun onLoggedOut()
    }
}