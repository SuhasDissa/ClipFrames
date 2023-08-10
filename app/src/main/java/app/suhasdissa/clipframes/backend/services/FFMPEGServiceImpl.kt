package app.suhasdissa.clipframes.backend.services

import android.content.Intent
import app.suhasdissa.clipframes.backend.models.ConverterState
import app.suhasdissa.clipframes.backend.models.FFMPEGCommand
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.util.FFMPEGUtil
import com.arthenica.ffmpegkit.FFmpegKit

class FFMPEGServiceImpl : FFMPEGService() {
    var onFFMPEGStatus: (FFMPEGStatus) -> Unit = {}

    override val notificationTitle: String
        get() = "Converting"

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        updateNotification()
        val extras = intent.extras
        extras?.let {
            val from = extras.getSerializable("command") as FFMPEGCommand?
            from?.let {
                runCatching {
                    converterState = ConverterState.ACTIVE
                    onConverterStateChanged(converterState)
                }
                FFMPEGUtil.processCommand(this,
                    command = it,
                    onFinished = { sessionResult ->
                        onFFMPEGStatus(sessionResult)
                        stopSelf()
                    }) { statistics ->
                    onFFMPEGStatus(FFMPEGStatus.Running(statistics))
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        FFmpegKit.cancel()
        super.onDestroy()
    }
}
