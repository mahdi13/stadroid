package com.stacrypt.stadroid.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stacrypt.stadroid.R
import com.stacrypt.stadroid.profile.banking.BankCardsFragment
import com.stacrypt.stadroid.profile.verification.EmailVerificationFragment

class ProfileSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val contentFragment: Fragment = when (intent.extras!![ARG_TARGET]) {
            TARGET_VERIFICATION_EMAIL -> EmailVerificationFragment()
            TARGET_BANK_CARDS -> BankCardsFragment()
            else -> throw IllegalArgumentException("Bad target")
        }

        val tx = supportFragmentManager.beginTransaction()
        tx.replace(R.id.container, contentFragment)
        tx.commitNow()
    }


    companion object {
        val ARG_TARGET = "target"
        val TARGET_VERIFICATION_EMAIL = "email_verification"
        val TARGET_BANK_CARDS = "bank_cards"
    }

}
