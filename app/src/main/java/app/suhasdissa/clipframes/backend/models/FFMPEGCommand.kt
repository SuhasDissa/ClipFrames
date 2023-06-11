package app.suhasdissa.clipframes.backend.models

import java.io.Serializable

sealed class FFMPEGCommand : Serializable {
    data class FFMPEGConvert(
        val inputFile: String,
        val videoCodec: String?,
        val audioCodec: String?,
        val extension: String,
        val outputFilePrefix: String,
    ) : FFMPEGCommand()

    data class FFMPEGReverse(
        val inputFile: String,
        val extension: String,
        val audio: Boolean,
        val video: Boolean,
        val outputFilePrefix: String,
    ) : FFMPEGCommand()

    data class FFMPEGSpeed(
        val inputFile: String,
        val speed: Float,
        val audioOnly: Boolean,
        val extension: String,
        val outputFilePrefix: String,
    ) : FFMPEGCommand()

    data class FFMPEGTrimmer(
        val inputFile: String,
        val extension: String,
        val startTimeStamp: String,
        val endTimeStamp: String,
        val outputFilePrefix: String,
    ) : FFMPEGCommand()
}
