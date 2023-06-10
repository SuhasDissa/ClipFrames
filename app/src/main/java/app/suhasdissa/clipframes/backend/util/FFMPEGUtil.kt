package app.suhasdissa.clipframes.backend.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParameters
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParametersReverse
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParametersSpeed
import app.suhasdissa.clipframes.backend.models.ffmpegparam.FFMPEGParametersTrimmer
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.MediaInformation
import com.arthenica.ffmpegkit.Statistics


object FFMPEGUtil {

    fun getMediaInfo(uri: Uri, context: Context): MediaInformation? {
        val inputVideoPath = FFmpegKitConfig.getSafParameterForRead(context, uri)
        val mediaInformation = FFprobeKit.getMediaInformation(inputVideoPath)
        return mediaInformation.mediaInformation
    }

    fun convert(
        context: Context,
        onFinished: (FFMPEGStatus) -> Unit,
        ffmpegParameters: FFMPEGParameters,
        onStatistics: (Statistics) -> Unit
    ) {
        val inputFilePath =
            FFmpegKitConfig.getSafParameterForRead(context, ffmpegParameters.inputFile.toUri());
        val outputFIlePath = StorageHelper.getOutputFile(
            ffmpegParameters.extension,
            ffmpegParameters.outputFilePrefix
        ).absolutePath

        val ffmpegCommand =
            "-i $inputFilePath" + (ffmpegParameters.videoCodec?.let { " -c:v $it" }
                ?: "") + (ffmpegParameters.audioCodec?.let { " -c:a $it" }
                ?: "") + " $outputFIlePath"

        ffmpegExecute(ffmpegCommand, onFinished, onStatistics)
    }

    fun reverse(
        context: Context,
        onFinished: (FFMPEGStatus) -> Unit,
        ffmpegParameters: FFMPEGParametersReverse,
        onStatistics: (Statistics) -> Unit
    ) {
        val inputFilePath =
            FFmpegKitConfig.getSafParameterForRead(context, ffmpegParameters.inputFile.toUri());
        val outputFIlePath = StorageHelper.getOutputFile(
            ffmpegParameters.extension,
            ffmpegParameters.outputFilePrefix
        ).absolutePath

        val ffmpegCommand =
            "-i $inputFilePath" + (if (ffmpegParameters.video) " -vf reverse" else "") + (if (ffmpegParameters.audio) " -af areverse" else "") + " $outputFIlePath"

        ffmpegExecute(ffmpegCommand, onFinished, onStatistics)

    }

    fun trimmer(
        context: Context,
        onFinished: (FFMPEGStatus) -> Unit,
        ffmpegParameters: FFMPEGParametersTrimmer,
        onStatistics: (Statistics) -> Unit
    ) {
        val inputFilePath =
            FFmpegKitConfig.getSafParameterForRead(context, ffmpegParameters.inputFile.toUri());
        val outputFIlePath = StorageHelper.getOutputFile(
            ffmpegParameters.extension,
            ffmpegParameters.outputFilePrefix
        ).absolutePath

        val ffmpegCommand =
            "-i $inputFilePath -ss ${ffmpegParameters.startTimeStamp} -to ${ffmpegParameters.endTimeStamp} -c copy $outputFIlePath"

        ffmpegExecute(ffmpegCommand, onFinished, onStatistics)
    }

    fun speedAdjust(
        context: Context,
        onFinished: (FFMPEGStatus) -> Unit,
        ffmpegParameters: FFMPEGParametersSpeed,
        onStatistics: (Statistics) -> Unit
    ) {
        val inputFilePath =
            FFmpegKitConfig.getSafParameterForRead(context, ffmpegParameters.inputFile.toUri());
        val outputFIlePath = StorageHelper.getOutputFile(
            ffmpegParameters.extension,
            ffmpegParameters.outputFilePrefix
        ).absolutePath

        val videoSpeed = 1 / ffmpegParameters.speed
        val audioSpeed = ffmpegParameters.speed
        val ffmpegCommand =
            "-i $inputFilePath" +
                    (if (ffmpegParameters.audioOnly) "" else " -filter:v \"setpts=${
                        String.format(
                            "%.2f",
                            videoSpeed
                        )
                    }*PTS\"") +
                    " -filter:a \"atempo=${String.format("%.2f", audioSpeed)}\"  $outputFIlePath"

        ffmpegExecute(ffmpegCommand, onFinished, onStatistics)
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
        FFmpegKit.executeAsync(ffmpegCommand,
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
            }, {
                Log.d(
                    "FFMPEG Log",
                    it.message
                )
            }, {
                onStatistics(it)
            }
        )
    }
}