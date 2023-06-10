package app.suhasdissa.clipframes

import android.app.Application
import app.suhasdissa.clipframes.backend.util.NotificationHelper

class ClipFramesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.buildNotificationChannels(this)
    }
}