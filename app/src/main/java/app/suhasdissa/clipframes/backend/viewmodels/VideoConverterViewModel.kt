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
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.models.VideoCodec
import app.suhasdissa.clipframes.backend.models.VideoExtensions
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParameters
import app.suhasdissa.clipframes.backend.services.ConverterService
import app.suhasdissa.clipframes.backend.services.FFMPEGService
import com.arthenica.ffmpegkit.MediaInformation

class VideoConverterViewModel : ViewModel() {
    private var inputFile by mutableStateOf<Uri?>(null)

    var inputFileMediaInfo by mutableStateOf<MediaInformation?>(null)

    var ffmpegStatus by mutableStateOf<FFMPEGStatus?>(null)

    var videoCodec by mutableStateOf<VideoCodec?>(null)
    var audioCodec by mutableStateOf<AudioCodec?>(null)

    var fileExtension by mutableStateOf(VideoExtensions.all.first())

    fun setInputFileUri(uri: Uri, context: Context) {
        inputFile = uri
        //inputFileMediaInfo = FFMPEGUtil.getMediaInfo(uri, context)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val ffmpegService = (service as FFMPEGService.LocalBinder).getService()
            (ffmpegService as ConverterService).onFFMPEGStatus = {
                ffmpegStatus = it
            }

        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

    fun startConverter(context: Context, outputFilePrefix: String) {
        inputFile?.let { inputFile ->
            val ffmpegParameters = FFMPEGParameters(
                inputFile.toString(),
                videoCodec = videoCodec?.codec,
                audioCodec = audioCodec?.codec,
                extension = fileExtension.extension,
                outputFilePrefix = outputFilePrefix
            )
            val serviceIntent = Intent(context, ConverterService::class.java)
            serviceIntent.putExtra("parameters", ffmpegParameters)
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
            context.stopService(Intent(context, ConverterService::class.java))
        }

        ContextCompat.startForegroundService(context, intent)
        Log.d("ViewModel", "Service Intent Sent")
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        Log.d("ViewModel", "Service Bound")
    }
}