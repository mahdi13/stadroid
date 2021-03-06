package io.stacrypt.stadroid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.core.CrashlyticsCore
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent

lateinit var application: Application

class Application : android.app.Application() {
    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlyticsKit)
       Instabug.Builder(this, "5b0b628ed1c493b59e0e059e03506dbb")
           .setInvocationEvents(InstabugInvocationEvent.FLOATING_BUTTON, InstabugInvocationEvent.SCREENSHOT)
           .build()

        // Init notification channels:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "General channel"
            val descriptionText = "General channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("general", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}

fun Context.copyToClipboard(label: String, text: String): ClipData? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(label, text)
    clipboard!!.primaryClip = clip
    return clip
}
