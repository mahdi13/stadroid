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

    /**
     * Here is a method which I defined to be able to achieve a lot of functionalities using these extra parameters.
     * The reseon of all of this structure is all profile related pages have been organized under a singe
     * activity {@class ProfileSettingActivity} but we would like to be able to interact properly to all related pages.
     *
     * For example assume we want to:
     *  * Navigate normally to different profile pages (with a hirarciale method)
     *  * Send the user to add a bank account for himself
     *  * Send the user to choose one of it's bank accounts
     *  * ...
     *
     * The main parameters are:
     *
     * {@property ARG_TARGET} -> Where do you want to go? (e.g. bank card lists -> TARGET_BANK_CARDS)
     *
     * {@property ARG_ACTION} -> What you want to do? (e.g. choose a card -> ACTION_CHOOSE)
     *
     * {@property ARG_LAUNCH_MODE} -> How do you want to go there?
     * (e.g. do a specific action and come back -> LAUNCH_MODE_DIALOG)
     *
     * {@property ARG_TOKEN} -> Mostly being used when we want to handle link click, from outside of the app
     * (Deep Linking) (e.g. client clicks on verify link on his received email on his inbox)

     * {@property ARG_RESULT} -> Wherever we want to respond a user, we use this key and the sender could get the
     * related data using this key while overriding {@override onActivityResult}
     *
     */
    companion object {
        const val ARG_TARGET = "target"
        const val ARG_ACTION = "action"
        const val ARG_TOKEN = "token"
        const val ARG_LAUNCH_MODE = "launch_mode"
        const val ARG_RESULT = "result"

        const val ACTION_CHOOSE = "choose"
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
        const val TARGET_ADD_BANK_ACCOUNT = "add_bank_account"

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

        const val RESULT_CHOOSE = "choose"
    }
}
