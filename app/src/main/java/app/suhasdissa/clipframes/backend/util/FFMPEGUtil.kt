package app.suhasdissa.clipframes.backend.util

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import app.suhasdissa.clipframes.backend.models.FFMPEGCommand
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.Statistics

object FFMPEGUtil {
    fun processCommand(
        context: Context,
        onFinished: (FFMPEGStatus) -> Unit,
        command: FFMPEGCommand,
        onStatistics: (Statistics) -> Unit
    ) {
        val inputFilePath =
            FFmpegKitConfig.getSafParameterForRead(context, command.inputFile.toUri())
        val outputFIlePath =
            StorageHelper.getOutputFile(command.extension, "ClipFrames").absolutePath

        var ffmpegCommand = ""
        command.trimTimestamps?.let {
            ffmpegCommand += "-ss ${it.startTimeStamp} -to ${it.endTimeStamp} "
        }
        command.speed?.let {
            if (it.video) {
                val videoSpeed = 1 / it.speed
                ffmpegCommand += "-filter:v \"setpts=${String.format("%.2f", videoSpeed)}*PTS\" "
            }
            if (it.audio) {
                val audioSpeed = it.speed
                ffmpegCommand += "-filter:a \"atempo=${String.format("%.2f", audioSpeed)}\" "
            }
        }
        command.reverse?.let {
            if (it.video) {
                ffmpegCommand += "-vf reverse "
            }
            if (it.audio) {
                ffmpegCommand += "-af areverse "
            }
        }
        command.videoCodec?.let {
            ffmpegCommand += "-c:v $it "
        }
        command.audioCodec?.let {
            ffmpegCommand += "-c:a $it "
        }

        val finalCommand = "-i $inputFilePath $ffmpegCommand $outputFIlePath"
        Log.e("FFMPEG COMMAND", finalCommand)
        ffmpegExecute(finalCommand, onFinished, onStatistics)
    }

    private fun ffmpegExecute(
        ffmpegCommand: String,
        onFinished: (FFMPEGStatus) -> Unit,
        onStatistics: (Statistics) -> Unit
    ) {
        Log.d(
            "FFMPEG Command",
            ffmpegCommand
        )
        FFmpegKit.executeAsync(
            ffmpegCommand,
            { session ->
                Log.d(
                    "FFMPEG Stopped",
                    "FFmpeg process exited with state ${session.state} and rc ${session.returnCode}"
                )
                val sessionResult =
                    if (session.returnCode.isValueCancel) {
                        FFMPEGStatus.Cancelled
                    } else if (session.returnCode.isValueSuccess) {
                        FFMPEGStatus.Success
                    } else {
                        FFMPEGStatus.Error
                    }
                onFinished(sessionResult)
            },
            {
                Log.d(
                    "FFMPEG Log",
                    it.message
                )
            },
            {
                onStatistics(it)
            }
        )
    }
}
