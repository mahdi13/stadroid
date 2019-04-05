package io.stacrypt.stadroid.profile.verification

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.profile.BaseSettingFragment
import kotlinx.android.synthetic.main.verification_process_fragment.*
import org.jetbrains.anko.support.v4.withArguments

class VerificationProcessFragment : BaseSettingFragment() {

    private lateinit var viewModel: VerificationProcessViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.verification_process_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(VerificationProcessViewModel::class.java)

        viewModel.client.observe(this, Observer {
            if (!it.isEmailVerified) {
                // Step 1
                stepper.stepCount = 4
                stepper.currentStep = 0
                childFragmentManager.beginTransaction()
                    .replace(R.id.verification_container, EmailVerificationFragment()).commitNow()
//                childFragmentManager.beginTransaction().replace(R.id.verification_container, MobilePhoneVerificationFragment()).commitNow()
//                childFragmentManager.beginTransaction().replace(R.id.verification_container, DoMobilePhoneVerificationFragment()).commitNow()
            } else {
                viewModel.evidence.observe(this, Observer { evidence ->
                    stepper.stepCount = 4
                    if (evidence.mobilePhone == null) {
                        stepper.currentStep = 1
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, MobilePhoneVerificationFragment()).commitNow()
                    } /* else if(evidence.fixedPhone == null) {
                        stepper.currentStep = 2
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, MobilePhoneVerificationFragment()).commitNow()

                    }*/ /*else if (
                        evidence.firstName == null ||
                        evidence.lastName == null ||
                        evidence.birthday == null ||
                        evidence.cityId == null ||
                        evidence.address == null ||
                        evidence.nationalCode == null ||
                        evidence.gender == null
                    ) {
                        stepper.currentStep = 2
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, EvidenceFormFragment()).commitNow()

                    } else if (evidence.idCard == null) {
                        stepper.currentStep = 3
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, MobilePhoneVerificationFragment()).commitNow()

                    } else if (evidence.idCardSecondary == null) {
                        stepper.currentStep = 4
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, MobilePhoneVerificationFragment()).commitNow()

                    } */ else if (
                        evidence.firstName == null ||
                        evidence.lastName == null ||
                        evidence.birthday == null ||
                        evidence.cityId == null ||
                        evidence.address == null ||
                        evidence.nationalCode == null ||
                        evidence.gender == null
                    ) {
                        // FIXME: Not a great condition
                        stepper.currentStep = 2
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, EvidenceFormFragment()).commitNow()
                    } else if (!it.isEvidenceVerified) {
                        // FIXME: Not a great condition
                        stepper.currentStep = 3
                        childFragmentManager.beginTransaction()
                            .replace(R.id.verification_container, VerificationPendingFragment()).commitNow()
                    } else {
                        stepper.currentStep = 3
                        stepper.setShowDoneIcon(true)
//                        childFragmentManager.beginTransaction()
//                            .replace(R.id.verification_container, EvidenceFormFragment()).commitNow()
                    }
                })
            }
        })

        viewModel.mobilePhoneSmsSent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { phone ->
                childFragmentManager.beginTransaction()
                    .replace(
                        R.id.verification_container,
                        DoMobilePhoneVerificationFragment().withArguments("phone" to phone)
                    ).commit()
            }
        })

        viewModel.doMobilePhoneSmsExpired.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { phone ->
                childFragmentManager.beginTransaction()
                    .replace(
                        R.id.verification_container,
                        MobilePhoneVerificationFragment().withArguments("phone" to phone)
                    ).commit()
            }
        })

        viewModel.doMobilePhoneSmsVerified.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { _ ->
                childFragmentManager.beginTransaction()
                    .replace(
                        R.id.verification_container,
                        EvidenceFormFragment()
                    ).commit()
            }
        })
    }
}
