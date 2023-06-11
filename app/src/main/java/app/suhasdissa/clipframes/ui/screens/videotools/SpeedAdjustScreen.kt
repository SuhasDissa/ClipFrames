package app.suhasdissa.clipframes.ui.screens.videotools

import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.R
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.viewmodels.SpeedAdjustViewModel
import app.suhasdissa.clipframes.ui.components.AudioPlayer
import app.suhasdissa.clipframes.ui.components.ScaffoldWithFAB
import app.suhasdissa.clipframes.ui.components.VideoPlayer


@Composable
fun SpeedAdjustScreen(
    converterViewModel: SpeedAdjustViewModel = viewModel(),
    audioOnly: Boolean
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.inputFile = uri
            }
        }
    var speed by remember { mutableStateOf(1f) }
    ScaffoldWithFAB(
        title = (if (audioOnly) R.string.change_audio_speed else R.string.change_video_speed),
        onFileOpen = { launcher.launch(arrayOf(if (audioOnly) "audio/*" else "video/*")) },
        onAction = {
            converterViewModel.startSpeedAdjust(
                context = context,
                extension = if (audioOnly) "mp3" else "mp4",
                speed = speed,
                audioOnly = audioOnly
            )
        },
        actionAllowed = true
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            converterViewModel.inputFile?.let {

                item {
                    if (audioOnly) {
                        AudioPlayer(uri = it, context = context)
                    } else {
                        VideoPlayer(uri = it, context = context)
                    }
                }
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Speed:")
                        Text("$speed")
                    }
                    Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.5f..2f)
                }
            }
            converterViewModel.ffmpegStatus?.let {
                item {
                    when (it) {
                        is FFMPEGStatus.Error -> Text("Some error occured")
                        is FFMPEGStatus.Success -> Text("Converting Success")
                        is FFMPEGStatus.Cancelled -> Text("Converting Cancelled")
                        is FFMPEGStatus.Running -> it.statistics.let {
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
        }
    }
}