package com.stacrypt.stadroid

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent

lateinit var application: Application

class Application : android.app.Application() {
    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        Instabug.Builder(this, "5b0b628ed1c493b59e0e059e03506dbb")
            .setInvocationEvents(InstabugInvocationEvent.FLOATING_BUTTON, InstabugInvocationEvent.SCREENSHOT)
            .build()

    }
}

fun Context.copyToClipboard(label: String, text: String): ClipData? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(label, text)
    clipboard!!.primaryClip = clip
    return clip
}
