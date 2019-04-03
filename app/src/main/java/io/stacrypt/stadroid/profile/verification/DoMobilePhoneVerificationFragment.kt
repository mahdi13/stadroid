package io.stacrypt.stadroid.profile.verification


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Event

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.android.synthetic.main.fragment_do_mobile_phone_verification.view.*
import kotlinx.android.synthetic.main.fragment_mobile_phone_verification.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DoMobilePhoneVerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_do_mobile_phone_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.verify.setOnClickListener {

            val otp = view.otp_view.otp?.takeIf { it.isNotEmpty() }?.takeIf { it.length == 6 }
            if (otp == null) {
                view.otp_view.showError()
                return@setOnClickListener
            }

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    stemeraldApiClient.verifyMobilePhoneVerification(
                        phone = arguments?.getString("phone")!!,
                        code = otp
                    ).await()
                    view.longSnackbar("Code verified!")
//                    viewModel.mobilePhoneSmsSent.postValue(Event(phone))
                } catch (e: Exception) {
                    e.printStackTrace()
                    view.longSnackbar("Error occurred!")
                }
            }

        }
    }
}
