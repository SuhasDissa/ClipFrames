package app.suhasdissa.clipframes.ui.screens.videotools

import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
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
import app.suhasdissa.clipframes.backend.viewmodels.ReverseViewModel
import app.suhasdissa.clipframes.ui.components.ScaffoldWithFAB
import app.suhasdissa.clipframes.ui.components.VideoPlayer

@Composable
fun VideoReverseScreen(converterViewModel: ReverseViewModel = viewModel()) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.inputFile = uri
            }
        }
    var reverseAudio by remember { mutableStateOf(false) }
    var reverseVideo by remember { mutableStateOf(false) }
    ScaffoldWithFAB(
        title = R.string.reverse_video,
        onFileOpen = { launcher.launch(arrayOf("video/*")) },
        onAction = {
            converterViewModel.startReversing(context, reverseAudio, reverseVideo, "mp4")
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
                    VideoPlayer(uri = it, context = context)
                }
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reverse Video")
                    Switch(checked = reverseVideo, onCheckedChange = {
                        reverseVideo = it
                    })
                }
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reverse Audio")
                    Switch(checked = reverseAudio, onCheckedChange = {
                        reverseAudio = it
                    })
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
