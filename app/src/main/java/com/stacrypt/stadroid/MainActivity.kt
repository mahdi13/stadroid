package com.stacrypt.stadroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.jetbrains.anko.toast
import java.lang.Exception


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    message.setText(R.string.title_wallet)
                }
                R.id.navigation_dashboard -> {
                    message.setText(R.string.title_market)
                }
                R.id.navigation_notifications -> {
                    message.setText(R.string.title_profile)
                }
            }
            true
        }


    }

    override fun onStart() {
        super.onStart()
        stexchangeApiClient.start()

        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                try {
                    toast(stexchangeApiClient.ping())
                } catch (e: Exception) {
                    toast(e.toString())
                    delay(2000)
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        stexchangeApiClient.shutdown()
    }
}


