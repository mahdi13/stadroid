package io.stacrypt.stadroid.profile


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ACTION_VERIFY
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_ACTION
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TOKEN
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_APPLICATION_PIN
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_ACCOUNTS
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_CHANEG_PASSWORD
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_DO_VERIFY_EMAIL
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_VERIFICATION_EMAIL
import io.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_VERIFICATION_PROCESS

class SettingEntrypointFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProfileSettingActivity) {

            val targetNavigationId: Int =
                if (context.intent?.data?.path?.contains("email_verification") == true) {
                    R.id.action_settings_entrypoint_fragment_to_do_verify_email_fragment
                } else {
                    when (context.intent.extras!![ARG_TARGET] as String) {
//                        TARGET_VERIFICATION_EMAIL -> R.id.action_settings_entrypoint_fragment_to_bank_cards_fragment
                        TARGET_BANK_CARDS -> R.id.action_settings_entrypoint_fragment_to_bank_cards_fragment
                        TARGET_BANK_ACCOUNTS -> R.id.action_settings_entrypoint_fragment_to_bank_accounts_fragment
                        TARGET_APPLICATION_PIN -> R.id.action_settings_entrypoint_fragment_to_app_security_fragment
                        TARGET_CHANEG_PASSWORD -> R.id.action_settings_entrypoint_fragment_to_change_password_fragment
                        TARGET_VERIFICATION_PROCESS -> R.id.action_settings_entrypoint_fragment_to_verification_process_fragment
                        TARGET_DO_VERIFY_EMAIL -> R.id.action_settings_entrypoint_fragment_to_do_verify_email_fragment
                        else -> throw IllegalArgumentException("Bad target")
                    }
                }


            var bundle: Bundle? = null
            if (context.intent.data != null && context.intent.data?.getQueryParameter("t_") != null) {
                bundle = Bundle().apply {
                    putString(ARG_ACTION, ACTION_VERIFY)
                    putString(ARG_TOKEN, context.intent.data?.getQueryParameter("t_"))
                }
            }

            NavHostFragment.findNavController(this).navigate(targetNavigationId, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return TextView(activity)
    }

}
