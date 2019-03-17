package com.stacrypt.stadroid.profile.verification

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.sessionManager
import kotlinx.android.synthetic.main.email_verification_fragment.*
import kotlinx.android.synthetic.main.email_verification_fragment.view.*
import android.content.Intent
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.DialogFragment
import com.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception


class EmailVerificationFragment : Fragment() {

    companion object {
        fun newInstance() = EmailVerificationFragment()
    }

    private lateinit var viewModel: EmailVerificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_verification_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).title = ""

        view.email.text = sessionManager.getPayload().email

        verify.setOnClickListener {
            //            GlobalScope.launch(Dispatchers.Main) {
//                try {
//                    stemeraldApiClient.schedulEmailVerification().await()
//                    toast("Verification email sent, check your email.")
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    toast(R.string.problem_occurred_toast)
//                }
//            }
        }
        verify.setOnClickListener {
            verify.startAnimation {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        stemeraldApiClient.schedulEmailVerification().await()
                        view.longSnackbar("Verification email sent", "Open email") {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                            startActivity(intent)
//                            startActivity(Intent.createChooser(intent, "Choose your email application"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(R.string.problem_occurred_toast)
                    } finally {
                        verify.doneLoadingAnimation(
                            resources.getColor(R.color.real_green),
                            resources.getDrawable(R.drawable.ic_check_circle_black_24dp).toBitmap(100, 100)
                        )
                    }
                }
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(EmailVerificationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

