package com.stacrypt.stadroid

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat

lateinit var application: Application

class Application : android.app.Application() {
    init {
        application = this
    }

}

fun Context.copyToClipboard(label: String, text: String): ClipData? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(label, text)
    clipboard!!.primaryClip = clip
    return clip
}
