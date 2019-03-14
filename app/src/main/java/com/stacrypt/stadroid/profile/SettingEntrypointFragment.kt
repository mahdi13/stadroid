package com.stacrypt.stadroid.profile


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.ARG_TARGET
import com.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_APPLICATION_PIN
import com.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_BANK_CARDS
import com.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_CHANEG_PASSWORD
import com.stacrypt.stadroid.profile.ProfileSettingActivity.Companion.TARGET_VERIFICATION_EMAIL

class SettingEntrypointFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProfileSettingActivity) {
            val targetNavigationId: Int = when (context.intent.extras!![ARG_TARGET] as String) {
                TARGET_VERIFICATION_EMAIL -> R.id.action_settings_entrypoint_fragment_to_bank_cards_fragment
                TARGET_BANK_CARDS -> R.id.action_settings_entrypoint_fragment_to_bank_cards_fragment
                TARGET_APPLICATION_PIN -> R.id.action_settings_entrypoint_fragment_to_app_security_fragment
                TARGET_CHANEG_PASSWORD -> R.id.action_settings_entrypoint_fragment_to_change_password_fragment
                else -> throw IllegalArgumentException("Bad target")
            }
            NavHostFragment.findNavController(this).navigate(targetNavigationId)
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
