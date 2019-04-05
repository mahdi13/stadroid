package io.stacrypt.stadroid.profile.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.stemeraldApiClient
import io.stacrypt.stadroid.profile.ProfileSettingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar

class DoVerifyEmailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_do_verify_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = stemeraldApiClient
                    .verifyEmailVerification(token = arguments?.getString(ProfileSettingActivity.ARG_TOKEN)!!)
                    .await()
                view.longSnackbar("Verification successful!")
            } catch (e: Exception) {
                e.printStackTrace()
                view.longSnackbar("Error occurred!")
            }
        }
    }
}
