package io.stacrypt.stadroid.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import io.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import org.jetbrains.anko.support.v4.alert

abstract class BaseSettingFragment : Fragment() {

    open fun authorize(): Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Check weather the user has access to this page
        if (!authorize()) {
            alert {
                isCancelable = false
                title = "There is one thing to do..."
                message = "Before continue to this page you should complete your verification process"
                positiveButton("Let's go") {
                    NavHostFragment.findNavController(this@BaseSettingFragment).navigateUp()
                    NavHostFragment.findNavController(this@BaseSettingFragment)
                        .navigate(R.id.action_global_verification_process_fragment)
//                    startActivity<ProfileSettingActivity>(
//                        ProfileSettingActivity.ARG_TARGET to ProfileSettingActivity.TARGET_VERIFICATION_PROCESS
//                    )
                }
                negativeButton("Cancel") { back() }
            }.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (view.findViewById<View>(R.id.back) != null) {
            view.back.setOnClickListener { back() }
        }
    }

    open fun back() {
        if (arguments?.getString(ProfileSettingActivity.ARG_LAUNCH_MODE) == ProfileSettingActivity.LAUNCH_MODE_DIALOG) {
            // It is an external call
            activity!!.finish()
        } else {
            NavHostFragment.findNavController(this@BaseSettingFragment).navigateUp()
        }
    }
}
