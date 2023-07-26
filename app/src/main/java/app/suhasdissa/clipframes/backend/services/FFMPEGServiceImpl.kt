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
                when (it) {
                    is FFMPEGCommand.FFMPEGConvert -> Convert(it)
                    is FFMPEGCommand.FFMPEGReverse -> Reverse(it)
                    is FFMPEGCommand.FFMPEGTrimmer -> Trim(it)
                    is FFMPEGCommand.FFMPEGSpeed -> SpeedAdjust(it)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun Convert(ffmpegConvert: FFMPEGCommand.FFMPEGConvert) {
        FFMPEGUtil.convert(
            this,
            ffmpegParameters = ffmpegConvert,
            onFinished = { sessionResult ->
                onFFMPEGStatus(sessionResult)
                stopSelf()
            }
        ) { statistics ->
            onFFMPEGStatus(FFMPEGStatus.Running(statistics))
        }
    }

    private fun Reverse(ffmpegReverse: FFMPEGCommand.FFMPEGReverse) {
        FFMPEGUtil.reverse(
            this,
            ffmpegParameters = ffmpegReverse,
            onFinished = { sessionResult ->
                onFFMPEGStatus(sessionResult)
                stopSelf()
            }
        ) { statistics ->
            onFFMPEGStatus(FFMPEGStatus.Running(statistics))
        }
    }

    private fun Trim(ffmpegTrimmer: FFMPEGCommand.FFMPEGTrimmer) {
        FFMPEGUtil.trimmer(
            this,
            ffmpegParameters = ffmpegTrimmer,
            onFinished = { sessionResult ->
                onFFMPEGStatus(sessionResult)
                stopSelf()
            }
        ) { statistics ->
            onFFMPEGStatus(FFMPEGStatus.Running(statistics))
        }
    }

    private fun SpeedAdjust(ffmpegSpeed: FFMPEGCommand.FFMPEGSpeed) {
        FFMPEGUtil.speedAdjust(
            this,
            ffmpegParameters = ffmpegSpeed,
            onFinished = { sessionResult ->
                onFFMPEGStatus(sessionResult)
                stopSelf()
            }
        ) { statistics ->
            onFFMPEGStatus(FFMPEGStatus.Running(statistics))
        }
    }

    override fun onDestroy() {
        FFmpegKit.cancel()
        super.onDestroy()
    }
}
