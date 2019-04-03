package io.stacrypt.stadroid.profile.verification

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Event
import androidx.lifecycle.ViewModelProviders

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.android.synthetic.main.fragment_mobile_phone_verification.*
import kotlinx.android.synthetic.main.fragment_mobile_phone_verification.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar
import retrofit2.HttpException


class MobilePhoneVerificationFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewModel: VerificationProcessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(VerificationProcessViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile_phone_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.ccp.registerCarrierNumberEditText(phone_number)
        verify.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val phone = ccp.selectedCountryCodeWithPlus + phone_number.text.toString().replace(" ", "")
                    stemeraldApiClient.schedulMobilePhoneVerification(
                        phone = phone
                    ).await()
                    view.longSnackbar("SMS Sent!")
                    viewModel.mobilePhoneSmsSent.postValue(Event(phone))
                } catch (e: HttpException) {
                    e.printStackTrace()
                    if (e.code() == 400) { // TODO: Check x-reason too
                        view.longSnackbar("Wrong code!")
                    } else {
                        view.longSnackbar("Error occurred!")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    view.longSnackbar("Error occurred!")
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
