package app.suhasdissa.clipframes.backend.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import app.suhasdissa.clipframes.MainActivity
import app.suhasdissa.clipframes.R
import app.suhasdissa.clipframes.backend.models.ConverterState
import app.suhasdissa.clipframes.backend.util.NotificationHelper
import app.suhasdissa.clipframes.backend.util.PermissionHelper


abstract class FFMPEGService : Service() {
    open var converterState: ConverterState = ConverterState.IDLE
    var onConverterStateChanged: (ConverterState) -> Unit = {}
    private val binder = LocalBinder()
    abstract val notificationTitle: String

    private val converterReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra(ACTION_EXTRA_KEY)) {
                STOP_ACTION -> stopSelf()
            }
        }
    }

    override fun onCreate() {
        val notification = buildNotification()
        startForeground(NotificationHelper.CONVERTING_NOTIFICATION_ID, notification.build())
        registerReceiver(converterReceiver, IntentFilter(SERVICE_INTENT_ACTION))
    }

    override fun onBind(intent: Intent): IBinder = binder

    inner class LocalBinder : Binder() {
        // Return this instance of [BackgroundMode] so clients can call public methods
        fun getService(): FFMPEGService = this@FFMPEGService
    }

    override fun onDestroy() {
        Log.d("Service", "Stopping")
        NotificationManagerCompat.from(this)
            .cancel(NotificationHelper.CONVERTING_NOTIFICATION_ID)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        converterState = ConverterState.IDLE
        onConverterStateChanged(converterState)
        super.onDestroy()
    }

    private fun buildNotification(): NotificationCompat.Builder {
        val stopIntent = Intent(SERVICE_INTENT_ACTION).putExtra(ACTION_EXTRA_KEY, STOP_ACTION)
        val stopAction = NotificationCompat.Action.Builder(
            null,
            getString(R.string.stop),
            getPendingIntent(stopIntent, 2)
        )

        return NotificationCompat.Builder(
            this,
            NotificationHelper.CONVERTING_NOTIFICATION_CHANNEL
        )
            .setContentTitle(notificationTitle)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSilent(true)
            .setOngoing(converterState == ConverterState.ACTIVE)
            .addAction(stopAction.build())
            .setUsesChronometer(true)
            .setContentIntent(getActivityIntent())
    }

    @SuppressLint("MissingPermission")
    fun updateNotification() {
        if (!PermissionHelper.hasPermission(this, Manifest.permission.POST_NOTIFICATIONS)) return
        val notification = buildNotification().build()
        NotificationManagerCompat.from(this).notify(
            NotificationHelper.CONVERTING_NOTIFICATION_ID,
            notification
        )
    }

    private fun getPendingIntent(intent: Intent, requestCode: Int): PendingIntent =
        PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun getActivityIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            6,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }


    companion object {
        const val SERVICE_INTENT_ACTION = "app.suhasdissa.clipframes.SERVICE_ACTION"
        const val ACTION_EXTRA_KEY = "action"
        const val STOP_ACTION = "STOP"
    }
}