package com.stacrypt.stadroid.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stacrypt.stadroid.R
import kotlinx.android.synthetic.main.activity_profile_setting.*

class ProfileSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = ""

    }

    companion object {
        val ARG_TARGET = "target"

        val TARGET_VERIFICATION_PROCESS = "verification_process"
        val TARGET_VERIFICATION_EMAIL = "email_verification"
        val TARGET_FIXED_PHONE_VERIFICATION = "mobile_phone_verification"
        val TARGET_MOBILE_PHONE_VERIFICATION = "fixed_phone_verification"
        val TARGET_EVIDENCE_VERIFICATION = "evidence_verification"

        val TARGET_BANK_CARDS = "bank_cards"
        val TARGET_ADD_BANK_CARD = "add_bank_card"
        val TARGET_BANK_ACCOUNTS = "bank_accounts"
        val TARGET_ADD_BANK_ACCOUNT = "bank_accounts"

        val TARGET_APPLICATION_PIN = "application_pin"
        val TARGET_WIPE_CACHE = "whip_cache"

        val TARGET_WITHDRAWAL_PIN = "withdrawal_pin"
        val TARGET_WITHDRAW_LIMITAITONS = "withdrawal_limitations"

        val TARGET_SESSIONS = "bank_accounts"
        val TARGET_SECOND_FACTOR = "second_factor"
        val TARGET_API_KEYS = "second_factor"
        val TARGET_IP_WHITELIST = "second_factor"
        val TARGET_SECURITY_LOGS = "second_factor"
        val TARGET_CHANEG_PASSWORD = "second_factor"

        val TARGET_TICKETS = "tickets"
        val TARGET_TICKET_THREAD = "ticket_thread"
        val TARGET_NEW_TICKETS = "new_ticker"
    }

}
