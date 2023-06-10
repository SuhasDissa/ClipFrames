package app.suhasdissa.clipframes.backend.models.ffmpegparam

import java.io.Serializable

data class FFMPEGParameters(
    val inputFile: String,
    val videoCodec: String?,
    val audioCodec: String?,
    val extension: String,
    val outputFilePrefix: String,
) : Serializable
