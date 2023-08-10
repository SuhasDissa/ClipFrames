package app.suhasdissa.clipframes.backend.models

import java.io.Serializable

data class SpeedData(
    val speed: Float,
    val audio: Boolean,
    val video: Boolean
) : Serializable
