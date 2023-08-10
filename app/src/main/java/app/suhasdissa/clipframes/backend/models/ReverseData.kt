package app.suhasdissa.clipframes.backend.models

import java.io.Serializable

data class ReverseData(
    val audio: Boolean,
    val video: Boolean
) : Serializable
