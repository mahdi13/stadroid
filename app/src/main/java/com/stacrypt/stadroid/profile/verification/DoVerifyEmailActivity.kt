package com.stacrypt.stadroid.profile.verification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.data.stemeraldApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

class DoVerifyEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_verify_email)

        try {
            GlobalScope.launch(Dispatchers.Main) {
                val response = stemeraldApiClient
                    .verifyEmailVerification(token = intent.data.getQueryParameter("t_"))
                    .await()
                toast("Verification successful!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Error occurred!")
        }
    }
}
