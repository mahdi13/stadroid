package io.stacrypt.stadroid.profile.banking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.add_bank_card_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

class AddBankCardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_bank_card_fragment, container, false)
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
                        return@launch
                    }
                    view.save.isEnabled = true
                    longToast(R.string.toast_bank_card_added_successfully)
                    NavHostFragment.findNavController(this@AddBankCardFragment).navigateUp()
                }
            }
        }

//        activity!!.add.isVisible = false
//        activity!!.back.isVisible = true
//        activity!!.back.setOnClickListener {
//            NavHostFragment.findNavController(this@AddBankCardFragment).navigateUp()
//        }

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
