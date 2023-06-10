package app.suhasdissa.clipframes.backend.models.ffmpegparam

import java.io.Serializable

data class FFMPEGParametersTrimmer(
    val inputFile: String,
    val extension: String,
    val startTimeStamp: String,
    val endTimeStamp: String,
    val outputFilePrefix: String,
) : Serializable
