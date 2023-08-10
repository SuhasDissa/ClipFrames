package app.suhasdissa.clipframes.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus

@Composable
fun ConverterStatusDisplay(status: FFMPEGStatus, modifier: Modifier = Modifier) {
    Column(modifier) {
        when (status) {
            is FFMPEGStatus.Error -> Text("Some error occurred")
            is FFMPEGStatus.Success -> Text("Convert Success")
            is FFMPEGStatus.Cancelled -> Text("Convert Cancelled")
            is FFMPEGStatus.Running -> status.statistics.let {
                Text(
                    text = "Time: " + DateUtils.formatElapsedTime(
                        it.time.toLong().div(1000)
                    )
                )
                Text(text = "Bitrate: ${it.bitrate}")
                Text(text = "Speed: ${it.speed}")
                Text(text = "VideoFrameNumber: ${it.videoFrameNumber}")
            }
        }
    }
}
