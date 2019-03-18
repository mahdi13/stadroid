package io.stacrypt.stadroid.data

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.util.Base64
import com.google.gson.Gson
import io.stacrypt.stadroid.application

/**
 * Should get manually updated while login/logout
 * `null` means `not logged in`
 */

@SuppressLint("StaticFieldLeak")
object sessionManager {

    var jwtToken: String? = null
        set(value) {
            field = value
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext).edit()
                .putString("jwtToken", value).apply()
        }
        get() {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                .getString("jwtToken", null)
        }

    fun login(t: String) {
        jwtToken = t
    }

    fun logout() {
        jwtToken = null
        cookieJar.clear()
        cookieJar.clearSession()
    }

    fun isLoggedIn() = jwtToken != null

    fun getPayload(): JwtPayload = Gson().fromJson(
        Base64.decode(jwtToken?.split(".")!![1], Base64.NO_PADDING).toString(Charsets.UTF_8),
        JwtPayload::class.java
    )

    fun role(): String = getPayload().role

    fun isAdmin() = role() == "admin"
    fun isRealtor() = role() == "realtor"
    fun isClient() = role() == "client"

    fun bearerToken(): String? = jwtToken.run { "Bearer ${this}" }
}

data class JwtPayload(val sessionId: String, val userId: String, val email: String, val role: String)
