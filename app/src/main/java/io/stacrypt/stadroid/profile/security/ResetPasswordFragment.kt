package io.stacrypt.stadroid.profile.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.data.verboseLocalizedMessage
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import io.stacrypt.stadroid.profile.banking.isValidName
import io.stacrypt.stadroid.profile.banking.isValidPassword
import kotlinx.android.synthetic.main.fragment_reset_password.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import retrofit2.HttpException

class ResetPasswordFragment : Fragment() {

    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_reset_password, container, false)

        token = arguments?.getString(ProfileSettingActivity.ARG_TOKEN)!!

        // TODO: Validate token

        rootView.change_password.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            val progressDialog =
                indeterminateProgressDialog("Wait", "Resetting your password...").apply { show() }
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    stemeraldApiClient.doResetPasswordPassword(
                        token = token,
                        password = rootView.new_password.text?.toString()!!
                    ).await()
                    longToast("Successfully restarted. Please login")
                    activity?.finish()
                } catch (e: HttpException) {
                    e.printStackTrace()
                    longToast(e.verboseLocalizedMessage())
                } catch (e: Exception) {
                    e.printStackTrace()
                    longToast(R.string.problem_occurred_toast)
                } finally {
                    progressDialog.dismiss()
                }
            }
        }

        return rootView
    }

    private fun validateForm(): Boolean {
        when {
            view?.new_password?.text?.toString()?.isValidPassword() != true -> return false.apply {
                view?.new_password?.error =
                    "Invalid password"
            }
            view?.new_password?.text?.toString() != view?.repeat_password?.text?.toString() -> return false.apply {
                view?.new_password?.error =
                    "Password repeat doesn't match"
            }
        }
        return true
    }
}
