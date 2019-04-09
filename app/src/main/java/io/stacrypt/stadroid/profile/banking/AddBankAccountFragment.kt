package io.stacrypt.stadroid.profile.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.data.verboseLocalizedMessage
import io.stacrypt.stadroid.profile.BaseSettingFragment
import kotlinx.android.synthetic.main.add_bank_account_fragment.view.*
import kotlinx.android.synthetic.main.evidence_form_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.iban4j.CountryCode
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import retrofit2.HttpException
import java.lang.Exception

class AddBankAccountFragment : BaseSettingFragment() {

    override fun authorize(): Boolean = sessionManager.isTrustedClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_bank_account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.iban.addBankIbanTextFormatter()
        view.save.setOnClickListener {
            if (validateInputs()) {
                GlobalScope.launch(Dispatchers.Main) {
                    view.save.isEnabled = false
                    try {
                        stemeraldApiClient.addBankAccount(
                            iban = view.iban.text.toString().replace(" ", ""),
                            owner = view.owner.text.toString(),
                            fiatSymbol = "TIRR" // FIXME
                        ).await()
                    } catch (e: HttpException) {
                        e.printStackTrace()
                        if (e.code() == 409) {
                            toast("You have already submitted an account with theses information")
                        } else {
                            toast(e.verboseLocalizedMessage())
                        }
                        view.save.isEnabled = true
                        return@launch
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
        view?.iban?.error = null
        view?.owner?.error = null

        val iban = view?.iban?.text?.toString()?.toIbanOrNull()
        if (iban == null) {
            view?.iban?.error = getString(R.string.error_invalid_bank_account_number)
            return false
        } else if (!listOf(CountryCode.IR).contains(iban.countryCode)) {
            view?.iban?.error = "Country not supported"
            return false
        }

        if (view?.owner?.text?.toString()?.isValidBankingIdName() != true) {
            view?.owner?.error = getString(R.string.error_invalid_bank_account_owner)
            return false
        }

        return true
    }
}
