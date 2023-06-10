package app.suhasdissa.clipframes.backend.util

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import app.suhasdissa.clipframes.R

object NotificationHelper {
    const val CONVERTING_NOTIFICATION_CHANNEL = "active_recording"
    const val CONVERTING_FINISHED_N_CHANNEL = "recording_finished"
    const val CONVERTING_NOTIFICATION_ID = 1
    const val CONVERTING_FINISHED_N_ID = 2

    fun buildNotificationChannels(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        listOf(
            CONVERTING_NOTIFICATION_CHANNEL to R.string.active_converting,
            CONVERTING_FINISHED_N_CHANNEL to R.string.converting_finished
        ).forEach { (channelName, stringResource) ->
            val channelCompat = NotificationChannelCompat.Builder(
                channelName,
                NotificationManagerCompat.IMPORTANCE_HIGH
            )
                .setName(context.getString(stringResource))
                .setLightsEnabled(true)
                .setShowBadge(true)
                .setVibrationEnabled(true)
                .build()

            notificationManager.createNotificationChannel(channelCompat)
        }
    }
}