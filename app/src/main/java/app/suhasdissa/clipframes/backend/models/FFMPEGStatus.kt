package app.suhasdissa.clipframes.backend.models

import com.arthenica.ffmpegkit.Statistics

sealed class FFMPEGStatus {
    object Success : FFMPEGStatus()
    object Cancelled : FFMPEGStatus()
    object Error : FFMPEGStatus()
    data class Running(val statistics: Statistics) : FFMPEGStatus()
}
