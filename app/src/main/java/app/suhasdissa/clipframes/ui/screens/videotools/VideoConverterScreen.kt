package app.suhasdissa.clipframes.ui.screens.videotools

import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import app.suhasdissa.clipframes.backend.models.AudioCodec
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.models.FileExtension
import app.suhasdissa.clipframes.backend.models.VideoCodec
import app.suhasdissa.clipframes.backend.models.VideoExtensions
import app.suhasdissa.clipframes.backend.viewmodels.VideoConverterViewModel
import app.suhasdissa.clipframes.ui.components.AudioCodecDialog
import app.suhasdissa.clipframes.ui.components.FileExtensionDialog
import app.suhasdissa.clipframes.ui.components.ScaffoldWithFAB
import app.suhasdissa.clipframes.ui.components.VideoCodecDialog
import app.suhasdissa.clipframes.ui.components.VideoPlayer

@Composable
fun VideoConverterScreen(converterViewModel: VideoConverterViewModel = viewModel()) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.inputFile = uri
            }
        }
    var audioCodecDialog by remember { mutableStateOf(false) }
    var selectedAudioCodec: AudioCodec? by remember {
        mutableStateOf(converterViewModel.audioCodec)
    }
    if (audioCodecDialog) {
        AudioCodecDialog(
            onSubmitButtonClick = {
                selectedAudioCodec = it
                converterViewModel.audioCodec = selectedAudioCodec
            },
            onDismissRequest = { audioCodecDialog = false }
        )
    }

    var videoCodecDialog by remember { mutableStateOf(false) }
    var selectedVideoCodec: VideoCodec? by remember {
        mutableStateOf(converterViewModel.videoCodec)
    }
    if (videoCodecDialog) {
        VideoCodecDialog(
            onSubmitButtonClick = {
                selectedVideoCodec = it
                converterViewModel.videoCodec = selectedVideoCodec
            },
            onDismissRequest = { videoCodecDialog = false }
        )
    }
    var selectedExtension: FileExtension by remember {
        mutableStateOf(converterViewModel.fileExtension)
    }
    var fileExtensionDialog by remember { mutableStateOf(false) }
    if (fileExtensionDialog) {
        FileExtensionDialog(
            onSubmitButtonClick = {
                selectedExtension = it
                converterViewModel.fileExtension = selectedExtension
            },
            onDismissRequest = { fileExtensionDialog = false },
            defaultExtension = selectedExtension,
            allExtensions = VideoExtensions.all
        )
    }
    ScaffoldWithFAB(
        title = R.string.video_converter,
        onFileOpen = { launcher.launch(arrayOf("video/*")) },
        onAction = {
            converterViewModel.startConverter(context, "VideoConvert")
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
                ElevatedCard(
                    Modifier
                        .clickable(onClick = { videoCodecDialog = true })
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Video Codec:")
                        Text(
                            selectedVideoCodec?.name ?: "Default",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            item {
                ElevatedCard(
                    Modifier
                        .clickable(onClick = { audioCodecDialog = true })
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Audio Codec:")
                        Text(
                            selectedAudioCodec?.name ?: "Default",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            item {
                ElevatedCard(
                    Modifier
                        .clickable(onClick = { fileExtensionDialog = true })
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("File Extension:")
                        Text(
                            selectedExtension.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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
