package app.suhasdissa.clipframes.ui.screens.audiotools

import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.R
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.viewmodels.ReverseViewModel
import app.suhasdissa.clipframes.ui.components.AudioPlayer
import app.suhasdissa.clipframes.ui.components.ScaffoldWithFAB


@Composable
fun AudioReverseScreen(converterViewModel: ReverseViewModel = viewModel()) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.inputFile = uri
            }
        }
    ScaffoldWithFAB(
        title = R.string.audio_reverse,
        onFileOpen = { launcher.launch(arrayOf("audio/*")) },
        onAction = {
            converterViewModel.startReversing(
                context,
                reverseAudio = true,
                reverseVideo = false,
                "mp3"
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
                    AudioPlayer(uri = it, context = context)
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