package io.stacrypt.stadroid.profile

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import io.stacrypt.stadroid.R
import io.stacrypt.stadroid.data.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.padding
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import retrofit2.HttpException

class LoginFragment : Fragment() {
    private var listener: OnLoginInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.login.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            singletonAsync {
                try {
                    progress.visibility = View.VISIBLE
                    val token: TokenResponse =
                        stemeraldApiClient.login(email.text.toString(), password.text.toString()).await()
                    sessionManager.login(token.token)
                    toast(getString(R.string.already_logged_in_toast))
                    listener?.onLoggedIn()
                } catch (e: HttpException) {
                    toast(getString(R.string.error_login_toast))
                } finally {
                    progress.visibility = View.GONE
                }
            }
        }

        view.reset_password.setOnClickListener {
            alert {
                var emailEditText: TextView? = null
                customView {
                    verticalLayout {
                        padding = dip(16)

                        textView("Enter your email") { }

                        textView("You will receive a password reset link in your email.") { }

                        textInputLayout {
                            hint = "Email"
                            emailEditText = textInputEditText {
                                textSize = 16f
                                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            }
                        }
                    }
                }
                positiveButton("Send email") {
                    doSendSchedulePassword(emailEditText?.text?.toString() ?: "")
                }

                negativeButton("Cancel") {}

            }.show()
        }

        view.register.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            singletonAsync {
                try {
                    progress.visibility = View.VISIBLE
                    val user: User = stemeraldApiClient.registerNewClient(
                        email.text.toString(),
                        password.text.toString()
                    ).await()
                    longToast("${getString(R.string.success_register_toast)} ${user.email}!")
                    listener?.onRegistered()
                } catch (e: HttpException) {
                    toast(getString(R.string.error_register_toast))
                } finally {
                    progress.visibility = View.GONE
                }
            }
        }

        return view
    }

    private fun doSendSchedulePassword(email: String) {
        val progressDialog = indeterminateProgressDialog("Wait", "") {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    stemeraldApiClient.scheduleResetPassword(email = email).await()
                    longToast("Please check your email")
                } catch (e: HttpException) {
                    e.printStackTrace()
                    longToast(e.verboseLocalizedMessage())
                } catch (e: Exception) {
                    e.printStackTrace()
                    longToast(R.string.problem_occurred_toast)
                } finally {
                    this@indeterminateProgressDialog.dismiss()
                }
            }.apply { show() }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginInteractionListener) listener = context
    }

    override fun onDetach() {
        super.onDetach()
        singletonJob = null
        listener = null
    }

    interface OnLoginInteractionListener {
        fun onLoggedIn()
        fun onRegistered()
    }

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
}
