package app.suhasdissa.clipframes.backend.models.ffmpegparam

import java.io.Serializable

data class FFMPEGParametersReverse(
    val inputFile: String,
    val extension: String,
    val audio: Boolean,
    val video: Boolean,
    val outputFilePrefix: String,
) : Serializable
