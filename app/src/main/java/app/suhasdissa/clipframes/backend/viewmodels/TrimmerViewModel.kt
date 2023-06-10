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
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParametersTrimmer
import app.suhasdissa.clipframes.backend.services.FFMPEGService
import app.suhasdissa.clipframes.backend.services.TrimmerService
import com.arthenica.ffmpegkit.MediaInformation

class TrimmerViewModel : ViewModel() {
    var inputFile by mutableStateOf<Uri?>(null)
        private set

    var inputFileMediaInfo by mutableStateOf<MediaInformation?>(null)
    var ffmpegStatus by mutableStateOf<FFMPEGStatus?>(null)

    fun setInputFileUri(uri: Uri, context: Context) {
        inputFile = uri
        //inputFileMediaInfo = FFMPEGUtil.getMediaInfo(uri, context)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val ffmpegService = (service as FFMPEGService.LocalBinder).getService()
            (ffmpegService as TrimmerService).onFFMPEGStatus = {
                ffmpegStatus = it
            }

        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

    fun startTrimming(
        context: Context,
        extension: String,
        startTimestamp: String,
        endTimeStamp: String
    ) {
        inputFile?.let { inputFile ->
            val ffmpegParameters = FFMPEGParametersTrimmer(
                inputFile.toString(),
                extension = extension,
                outputFilePrefix = "Trimmed",
                startTimeStamp = startTimestamp,
                endTimeStamp = endTimeStamp
            )
            val serviceIntent = Intent(context, TrimmerService::class.java)
            serviceIntent.putExtra("parameters", ffmpegParameters)
            startconverterService(context, serviceIntent)
        }
    }

    private fun startconverterService(context: Context, intent: Intent) {
        runCatching {
            context.unbindService(connection)
        }
        runCatching {
            context.stopService(Intent(context, TrimmerService::class.java))
        }
        ContextCompat.startForegroundService(context, intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
}