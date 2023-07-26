package app.suhasdissa.clipframes.backend.viewmodels

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import app.suhasdissa.clipframes.backend.models.AudioCodec
import app.suhasdissa.clipframes.backend.models.AudioExtensions
import app.suhasdissa.clipframes.backend.models.FFMPEGCommand
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.services.FFMPEGService
import app.suhasdissa.clipframes.backend.services.FFMPEGServiceImpl

class AudioConverterViewModel : ViewModel() {
    var inputFile by mutableStateOf<Uri?>(null)

    var ffmpegStatus by mutableStateOf<FFMPEGStatus?>(null)

    var audioCodec by mutableStateOf<AudioCodec?>(null)

    var fileExtension by mutableStateOf(AudioExtensions.all.first())

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val ffmpegService = (service as FFMPEGService.LocalBinder).getService()
            (ffmpegService as FFMPEGServiceImpl).onFFMPEGStatus = {
                ffmpegStatus = it
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    fun startConverter(context: Context, outputFilePrefix: String) {
        inputFile?.let { inputFile ->
            val ffmpegParameters = FFMPEGCommand.FFMPEGConvert(
                inputFile.toString(),
                videoCodec = null,
                audioCodec = audioCodec?.codec,
                extension = fileExtension.extension,
                outputFilePrefix = outputFilePrefix
            )
            val serviceIntent = Intent(context, FFMPEGServiceImpl::class.java)
            serviceIntent.putExtra("command", ffmpegParameters)
            Log.d("ViewModel", "Service Intent Sending...")
            startconverterService(context, serviceIntent)
        } ?: {
            Toast.makeText(context, "Failed Opening File", Toast.LENGTH_SHORT)
        }
    }

    private fun startconverterService(context: Context, intent: Intent) {
        runCatching {
            context.unbindService(connection)
        }
        runCatching {
            context.stopService(Intent(context, FFMPEGServiceImpl::class.java))
        }

        ContextCompat.startForegroundService(context, intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
}
