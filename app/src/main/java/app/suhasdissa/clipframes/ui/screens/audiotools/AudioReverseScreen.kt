package app.suhasdissa.clipframes.ui.screens.audiotools

import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.viewmodels.ReverseViewModel


@Composable
fun AudioReverseScreen(converterViewModel: ReverseViewModel = viewModel()) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.setInputFileUri(uri, context)
            }
        }
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
            Button(onClick = { launcher.launch(arrayOf("audio/*")) }) {
                Text("Open Audio")
            }
        }
        item {
            Button(onClick = {
                converterViewModel.startReversing(
                    context,
                    reverseAudio = true,
                    reverseVideo = false,
                    "mp3"
                )
            }) {
                Text("Start Reversing")
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