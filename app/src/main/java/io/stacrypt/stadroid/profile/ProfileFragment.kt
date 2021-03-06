package io.stacrypt.stadroid.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_CHANGE_PASSWORD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_VERIFICATION_PROCESS
import io.stacrypt.stadroid.profile.banking.extractIpAddress
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColorResource
import java.util.Locale

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

        this?.logout?.setOnClickListener {
            sessionManager.logout()
            // TODO: Call logout service
            // TODO: Terminate current session
            // TODO: Clear entire db
            listener?.onLoggedOut()
        }

        this?.change_language?.setOnClickListener {
            selector(
                "Choose Language",
                resources.getStringArray(R.array.supported_languages).toList()
            ) { dialogInterface: DialogInterface, i: Int ->

                when (i) {
                    0 -> Locale.setDefault(Locale("fa"))
                    1 -> Locale.setDefault(Locale("en"))
                }
            }
        }

        this?.verification?.setTarget(TARGET_VERIFICATION_PROCESS)
        this?.bank_cards?.setTarget(TARGET_BANK_CARDS)
        this?.bank_accounts?.setTarget(TARGET_BANK_ACCOUNTS)
        this?.pin?.setTarget(TARGET_APPLICATION_PIN)
        this?.change_password?.setTarget(TARGET_CHANGE_PASSWORD)

        viewModel = ViewModelProviders.of(activity!!).get(ProfileViewModel::class.java)
        viewModel.user?.observe(viewLifecycleOwner,
            Observer<User> { user ->
                if (user == null) return@Observer
                this?.email?.text = user.email
            })

        viewModel.lastLogin.observe(viewLifecycleOwner,
            Observer { lastLogin ->
                if (lastLogin == null) return@Observer
                this?.last_login?.text = StringBuilder().append("Last login on ").append(
                    if (lastLogin.createdAt != null) lastLogin.createdAt?.format()
                    else "Unknown"
                )
                    .append(" By ")
                    .append(lastLogin.details?.extractIpAddress() ?: "Unknown")
            })


        when {
            sessionManager.isTrustedClient -> {
                this?.verification_status?.text = "Verified"
                this?.verification_status?.textColorResource = R.color.real_green
            }
            sessionManager.isSemiTrustedClient -> {
                this?.verification_status?.text = "Partially Verified"
                this?.verification_status?.textColorResource = R.color.yellow
            }
            sessionManager.isClient -> {
                this?.verification_status?.text = "Unverified"
                this?.verification_status?.textColorResource = R.color.real_red
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