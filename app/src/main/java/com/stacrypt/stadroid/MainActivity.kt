package com.stacrypt.stadroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

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
}
