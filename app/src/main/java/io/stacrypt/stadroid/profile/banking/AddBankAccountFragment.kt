package io.stacrypt.stadroid.profile.banking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.profile.BaseSettingFragment
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.add_bank_card_fragment.view.*
import kotlinx.android.synthetic.main.header_appbar_back.view.*
import kotlinx.android.synthetic.main.notification_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

class AddBankAccountFragment : BaseSettingFragment() {

    override fun authorize(): Boolean  = sessionManager.isTrustedClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_bank_account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.number.addBankCardTextFormatter()
        view.save.setOnClickListener {
            if (validateInputs()) {
                GlobalScope.launch(Dispatchers.Main) {
                    view.save.isEnabled = false
                    try {
                        stemeraldApiClient.addBankCard(
                            pan = view.number.text.toString(),
                            holder = view.holder.text.toString(),
                            fiatSymbol = "TIRR" // FIXME
                        ).await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(R.string.problem_occurred_toast)
                        view.save.isEnabled = true
                        return@launch
                    }
                    view.save.isEnabled = true
                    longToast(R.string.toast_bank_card_added_successfully)
                    back()
                }
            }
        }

    }

    private fun validateInputs(): Boolean {
        view?.number?.error = null
        view?.holder?.error = null

        if (view?.number?.text?.toString()?.isValidBankCardNumber() != true) {
            view?.number?.error = getString(R.string.error_invalid_bank_card_number)
            return false
        }

        if (view?.holder?.text?.toString()?.isValidBankingIdName() != true) {
            view?.holder?.error = getString(R.string.error_invalid_bank_card_holder)
            return false
        }

        return true
    }


}
