package app.suhasdissa.clipframes.backend.models.ffmpegparam

import java.io.Serializable

data class FFMPEGParametersSpeed(
    val inputFile: String,
    val speed: Float,
    val audioOnly: Boolean,
    val extension: String,
    val outputFilePrefix: String,
) : Serializable
