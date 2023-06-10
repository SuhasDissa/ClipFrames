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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.viewmodels.SpeedAdjustViewModel


@Composable
fun SpeedAdjustScreen(
    converterViewModel: SpeedAdjustViewModel = viewModel(),
    audioOnly: Boolean
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.setInputFileUri(uri, context)
            }
        }
    var speed by remember { mutableStateOf(1f) }
    LazyColumn(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            converterViewModel.inputFileMediaInfo?.let {
                ElevatedCard(
                    Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Opened File:")
                        Text(
                            it.filename,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        item {
            Button(onClick = { launcher.launch(arrayOf(if (audioOnly) "audio/*" else "video/*")) }) {
                Text(if (audioOnly) "Open Audio" else "Open Video")
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
        item {
            Button(onClick = {
                converterViewModel.startSpeedAdjust(
                    context = context,
                    extension = if (audioOnly) "mp3" else "mp4",
                    speed = speed,
                    audioOnly = audioOnly
                )
            }) {
                Text("Start Speed Adjust")
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