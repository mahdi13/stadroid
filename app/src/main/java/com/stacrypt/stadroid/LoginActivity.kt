package com.stacrypt.stadroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.stacrypt.stadroid.data.emeraldApiClient
import com.stacrypt.stadroid.data.sessionManager
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.coroutines.*

import org.jetbrains.anko.*
import retrofit2.HttpException


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Keep track of the login job.
     */
    private var singletonJob: Job? = null
        set(value) {
            if (field?.isActive == true) field?.cancel()
            field = value
            field?.start()
        }

    private fun singletonAsync(block: suspend CoroutineScope.() -> Unit) {
        singletonJob = GlobalScope.launch(Dispatchers.Main, CoroutineStart.LAZY) {
            block()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check whether the user is already login
        if (sessionManager.isLoggedIn()) {
            startActivity<MainActivity>()
            toast("${getString(R.string.success_login_toast)} ${sessionManager.getPayload().email}!")
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        signin.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            singletonAsync {
                try {
                    progress.visibility = View.VISIBLE
                    val user: User = apiClient!!.signin(email.text.toString(), password.text.toString()).await()
                    sessionManager.login(user.token!!)
                    toast(getString(R.string.already_logged_in_toast))
                    startActivity<MainActivity>()
                    finish()
                } catch (e: HttpException) {
                    toast(getString(R.string.error_login_toast))
                } finally {
                    progress.visibility = View.GONE
                }
            }
        }

        signout.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            singletonAsync {
                try {
                    progress.visibility = View.VISIBLE
                    val user: User = emeraldApiClient.signup(email.text.toString(), password.text.toString()).await()
                    sessionManager.login(user.token!!)
                    longToast("${getString(R.string.success_register_toast)} ${user.email}!")
                    startActivity<MainActivity>()
                    finish()
                } catch (e: HttpException) {
                    toast(getString(R.string.error_register_toast))
                } finally {
                    progress.visibility = View.GONE
                }
            }
        }

    }

    private fun isEmailValid(email: String) = email.contains("@")

    private fun isPasswordValid(password: String) = password.length > 4

    private fun validateInputs(): Boolean {

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            return false
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            return false
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            return false
        }

        return true

    }

    override fun onStop() {
        super.onStop()
        singletonJob = null
    }

}
