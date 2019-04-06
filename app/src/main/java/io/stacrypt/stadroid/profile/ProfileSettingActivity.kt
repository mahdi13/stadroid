package io.stacrypt.stadroid.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import io.stacrypt.stadroid.R

class ProfileSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
//        actionBar?.setDisplayHomeAsUpEnabled(true)

//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.title = ""
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        if (navHost != null)
            supportFragmentManager.beginTransaction().detach(navHost).attach(navHost)
    }

    companion object {
        const val ARG_TARGET = "target"
        const val ARG_ACTION = "action"
        const val ARG_TOKEN = "token"
        const val ARG_LAUNCH_MODE = "launch_mode"
        const val ARG_RESULT = "result"

        const val ACTION_VERIFY = "verify"

        // TODO Rename it to something meaningful (e.g. isExternalCall)
//        const val ACTION_ADD = "add" // This will make the fragment just like a dialog

        const val TARGET_VERIFICATION_PROCESS = "verification_process"
        const val TARGET_VERIFICATION_EMAIL = "email_verification"
        const val TARGET_DO_VERIFY_EMAIL = "do_verify_email"
        const val TARGET_FIXED_PHONE_VERIFICATION = "mobile_phone_verification"
        const val TARGET_MOBILE_PHONE_VERIFICATION = "fixed_phone_verification"
        const val TARGET_EVIDENCE_VERIFICATION = "evidence_verification"

        const val TARGET_BANK_CARDS = "bank_cards"
        const val TARGET_ADD_BANK_CARD = "add_bank_card"
        const val TARGET_BANK_ACCOUNTS = "bank_accounts"
        const val TARGET_ADD_BANK_ACCOUNT = "bank_accounts"

        const val TARGET_APPLICATION_PIN = "application_pin"
        const val TARGET_WIPE_CACHE = "whip_cache"

        const val TARGET_WITHDRAWAL_PIN = "withdrawal_pin"
        const val TARGET_WITHDRAW_LIMITAITONS = "withdrawal_limitations"

        const val TARGET_SESSIONS = "bank_accounts"
        const val TARGET_SECOND_FACTOR = "second_factor"
        const val TARGET_API_KEYS = "second_factor"
        const val TARGET_IP_WHITELIST = "second_factor"
        const val TARGET_SECURITY_LOGS = "second_factor"
        const val TARGET_CHANEG_PASSWORD = "second_factor"

        const val TARGET_TICKETS = "tickets"
        const val TARGET_TICKET_THREAD = "ticket_thread"
        const val TARGET_NEW_TICKETS = "new_ticker"

        const val LAUNCH_MODE_NORMAL = "normal"
        const val LAUNCH_MODE_DIALOG = "dialog"
    }
}
