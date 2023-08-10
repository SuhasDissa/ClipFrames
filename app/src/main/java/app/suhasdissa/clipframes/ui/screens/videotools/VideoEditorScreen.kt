package app.suhasdissa.clipframes.ui.screens.videotools

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.backend.models.AudioCodecs
import app.suhasdissa.clipframes.backend.models.AudioExtensions
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.models.FileExtension
import app.suhasdissa.clipframes.backend.models.ReverseData
import app.suhasdissa.clipframes.backend.models.SpeedData
import app.suhasdissa.clipframes.backend.models.VideoCodecs
import app.suhasdissa.clipframes.backend.models.VideoExtensions
import app.suhasdissa.clipframes.backend.viewmodels.FfmpegToolViewModel
import app.suhasdissa.clipframes.ui.components.ConverterStatusDisplay
import app.suhasdissa.clipframes.ui.components.ExpandableToggleSwitchRow
import app.suhasdissa.clipframes.ui.components.FileExtensionDialog
import app.suhasdissa.clipframes.ui.components.VideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(videoViewModel: FfmpegToolViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedExtension: FileExtension by remember {
        mutableStateOf(videoViewModel.fileExtension)
    }
    var speedEnabled by remember {
        mutableStateOf(false)
    }
    var fileExtensionDialog by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                videoViewModel.inputFile = uri
            }
        }
    LaunchedEffect(Unit) {
        if (videoViewModel.inputFile == null) {
            launcher.launch(arrayOf("video/*"))
        }
    }
    if (fileExtensionDialog) {
        FileExtensionDialog(
            onSubmitButtonClick = {
                selectedExtension = it
                videoViewModel.fileExtension = selectedExtension
            },
            onDismissRequest = { fileExtensionDialog = false },
            defaultExtension = selectedExtension,
            allExtensions = AudioExtensions.all
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Video Converter"
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            videoViewModel.inputFile?.let {
                item {
                    VideoPlayer(uri = it)
                }
            }
            item {
                var showCodec by remember { mutableStateOf(false) }
                ExpandableToggleSwitchRow(
                    title = "Change Video Codec:",
                    checked = showCodec,
                    onCheckedChange = {
                        showCodec = it
                    }) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = VideoCodecs.all) {
                            FilterChip(
                                selected = videoViewModel.videoCodec == it,
                                onClick = { videoViewModel.videoCodec = it },
                                label = { Text(it.name) }
                            )
                        }
                    }
                }
            }
            item {
                var showCodec by remember { mutableStateOf(false) }
                ExpandableToggleSwitchRow(
                    title = "Change Audio Codec:",
                    checked = showCodec,
                    onCheckedChange = {
                        showCodec = it
                    }) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = AudioCodecs.all) {
                            FilterChip(
                                selected = videoViewModel.audioCodec == it,
                                onClick = { videoViewModel.audioCodec = it },
                                label = { Text(it.name) }
                            )
                        }
                    }
                }
            }
            item {
                Column() {
                    Text("File Extension:", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = AudioExtensions.all + VideoExtensions.all) {
                            FilterChip(
                                selected = videoViewModel.fileExtension == it,
                                onClick = { videoViewModel.fileExtension = it },
                                label = { Text(it.name) }
                            )
                        }
                    }
                }
            }
            item {
                var reverse by remember { mutableStateOf(false) }
                ExpandableToggleSwitchRow(
                    title = "Reverse:",
                    checked = reverse,
                    onCheckedChange = {
                        reverse = it
                        if (!it) {
                            videoViewModel.reverseData = null
                        }
                    }) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        var reverseVideo by remember(videoViewModel.reverseData) {
                            mutableStateOf(videoViewModel.reverseData?.video ?: false)
                        }
                        var reverseAudio by remember(videoViewModel.reverseData) {
                            mutableStateOf(videoViewModel.reverseData?.audio ?: false)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Reverse Video:", style = MaterialTheme.typography.titleSmall)
                            Checkbox(checked = reverseVideo, onCheckedChange = {
                                reverseVideo = it
                                videoViewModel.reverseData = ReverseData(
                                    audio = reverseAudio,
                                    video = reverseVideo
                                )
                            })
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Reverse Audio:", style = MaterialTheme.typography.titleSmall)
                            Checkbox(checked = reverseAudio, onCheckedChange = {
                                reverseAudio = it
                                videoViewModel.reverseData = ReverseData(
                                    audio = reverseAudio,
                                    video = reverseVideo
                                )
                            })
                        }
                        Card {
                            Row(Modifier.padding(8.dp)) {
                                Text(
                                    "This feature uses a lot of device memory. and can cause the device to freeze and become unresponsive. Do not reverse large videos. Maximum of 30 second recommended.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            item {
                ExpandableToggleSwitchRow(
                    title = "Change Speed:",
                    checked = speedEnabled,
                    onCheckedChange = {
                        speedEnabled = it
                        if (!it) {
                            videoViewModel.speedData = null
                        }
                    }) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        var videoSpeed by remember(videoViewModel.speedData) {
                            mutableStateOf(videoViewModel.speedData?.video ?: false)
                        }
                        var audioSpeed by remember(videoViewModel.speedData) {
                            mutableStateOf(videoViewModel.speedData?.audio ?: false)
                        }
                        var speed by remember(videoViewModel.speedData) {
                            mutableStateOf(videoViewModel.speedData?.speed ?: 1f)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Video Speed:", style = MaterialTheme.typography.titleSmall)
                            Checkbox(checked = videoSpeed, onCheckedChange = {
                                videoSpeed = it
                                videoViewModel.speedData = SpeedData(
                                    speed = speed,
                                    video = videoSpeed,
                                    audio = audioSpeed
                                )
                            })
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Audio Speed:", style = MaterialTheme.typography.titleSmall)
                            Checkbox(checked = audioSpeed, onCheckedChange = {
                                audioSpeed = it
                                videoViewModel.speedData = SpeedData(
                                    speed = speed,
                                    video = videoSpeed,
                                    audio = audioSpeed
                                )
                            })
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Speed:")
                            Text("$speed")
                        }
                        Slider(
                            value = speed,
                            onValueChange = { speed = it },
                            valueRange = 0.5f..2f,
                            onValueChangeFinished = {
                                videoViewModel.speedData =
                                    SpeedData(speed, audio = audioSpeed, video = videoSpeed)
                            })
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { videoViewModel.startConverter(context) }) {
                        Text(text = "Start")
                    }
                }
            }
        }
    }
    videoViewModel.ffmpegStatus?.let {
        AlertDialog(onDismissRequest = {
            if (it !is FFMPEGStatus.Running) {
                videoViewModel.ffmpegStatus = null
            }
        },
            confirmButton = {
                Button(onClick = {
                    videoViewModel.ffmpegStatus = null
                }, enabled = (it !is FFMPEGStatus.Running)) {
                    Text("Close")
                }
            }, title = {
                Text(text = "Video Convert Status")
            }, text = {
                ConverterStatusDisplay(status = it)
            })
    }
}
