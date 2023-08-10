package app.suhasdissa.clipframes.backend.models

import java.io.Serializable

data class FFMPEGCommand(
    val inputFile: String,
    val videoCodec: String? = null,
    val audioCodec: String? = null,
    val extension: String,
    val trimTimestamps: TrimTimestamps? = null,
    val speed: SpeedData? = null,
    val reverse: ReverseData? = null
) : Serializable
