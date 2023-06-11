package app.suhasdissa.clipframes.backend.viewmodels

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParametersSpeed
import app.suhasdissa.clipframes.backend.services.FFMPEGService
import app.suhasdissa.clipframes.backend.services.SpeedAdjustService

class SpeedAdjustViewModel : ViewModel() {
    var inputFile by mutableStateOf<Uri?>(null)

    var ffmpegStatus by mutableStateOf<FFMPEGStatus?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val ffmpegService = (service as FFMPEGService.LocalBinder).getService()
            (ffmpegService as SpeedAdjustService).onFFMPEGStatus = {
                ffmpegStatus = it
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

    fun startSpeedAdjust(
        context: Context,
        extension: String,
        speed: Float,
        audioOnly: Boolean
    ) {
        inputFile?.let { inputFile ->
            val ffmpegParameters = FFMPEGParametersSpeed(
                inputFile.toString(),
                extension = extension,
                outputFilePrefix = "SpeedAdjust",
                speed = speed,
                audioOnly = audioOnly
            )
            val serviceIntent = Intent(context, SpeedAdjustService::class.java)
            serviceIntent.putExtra("parameters", ffmpegParameters)
            startconverterService(context, serviceIntent)
        }
    }

    private fun startconverterService(context: Context, intent: Intent) {
        runCatching {
            context.unbindService(connection)
        }
        runCatching {
            context.stopService(Intent(context, SpeedAdjustService::class.java))
        }
        ContextCompat.startForegroundService(context, intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
}