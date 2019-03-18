package io.stacrypt.stadroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.stacrypt.stadroid.data.sessionManager
import io.stacrypt.stadroid.profile.LoginFragment

import org.jetbrains.anko.*


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginFragment.OnLoginInteractionListener {
    override fun onLoggedIn() {
        startActivity<MainActivity>()
        finish()
    }

    override fun onRegistered() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check whether the user is already login
        if (sessionManager.isLoggedIn()) {
            startActivity<MainActivity>()
            toast("${getString(R.string.already_logged_in_toast)} ${sessionManager.getPayload().email}!")
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, LoginFragment())
        ft.commitNow()
    }

}
