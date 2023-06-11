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
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParametersReverse
import app.suhasdissa.clipframes.backend.services.FFMPEGService
import app.suhasdissa.clipframes.backend.services.ReverseService

class ReverseViewModel : ViewModel() {
    var inputFile by mutableStateOf<Uri?>(null)

    var ffmpegStatus by mutableStateOf<FFMPEGStatus?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val ffmpegService = (service as FFMPEGService.LocalBinder).getService()
            (ffmpegService as ReverseService).onFFMPEGStatus = {
                ffmpegStatus = it
            }

        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

    fun startReversing(
        context: Context,
        reverseAudio: Boolean,
        reverseVideo: Boolean,
        extension: String
    ) {
        inputFile?.let { inputFile ->
            val ffmpegParameters = FFMPEGParametersReverse(
                inputFile.toString(),
                extension = extension,
                outputFilePrefix = "Reversed",
                video = reverseVideo,
                audio = reverseAudio
            )
            val serviceIntent = Intent(context, ReverseService::class.java)
            serviceIntent.putExtra("parameters", ffmpegParameters)
            startconverterService(context, serviceIntent)
        }
    }

    private fun startconverterService(context: Context, intent: Intent) {
        runCatching {
            context.unbindService(connection)
        }
        runCatching {
            context.stopService(Intent(context, ReverseService::class.java))
        }
        ContextCompat.startForegroundService(context, intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
}