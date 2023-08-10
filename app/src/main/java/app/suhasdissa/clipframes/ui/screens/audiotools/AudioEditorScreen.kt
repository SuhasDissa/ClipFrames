package app.suhasdissa.clipframes.ui.screens.audiotools

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.backend.models.AudioCodecs
import app.suhasdissa.clipframes.backend.models.AudioExtensions
import app.suhasdissa.clipframes.backend.models.FileExtension
import app.suhasdissa.clipframes.backend.models.ReverseData
import app.suhasdissa.clipframes.backend.viewmodels.FfmpegToolViewModel
import app.suhasdissa.clipframes.ui.components.AudioPlayer
import app.suhasdissa.clipframes.ui.components.ConverterStatusDisplay
import app.suhasdissa.clipframes.ui.components.ExpandableToggleSwitchRow
import app.suhasdissa.clipframes.ui.components.FileExtensionDialog
import app.suhasdissa.clipframes.ui.components.ToggleSwitchRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEditorScreen(audioViewModel: FfmpegToolViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedExtension: FileExtension by remember {
        mutableStateOf(audioViewModel.fileExtension)
    }
    var fileExtensionDialog by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                audioViewModel.inputFile = uri
            }
        }
    LaunchedEffect(Unit) {
        if (audioViewModel.inputFile == null) {
            launcher.launch(arrayOf("audio/*"))
        }
    }
    if (fileExtensionDialog) {
        FileExtensionDialog(
            onSubmitButtonClick = {
                selectedExtension = it
                audioViewModel.fileExtension = selectedExtension
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
                        "Audio Converter"
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
            audioViewModel.inputFile?.let {
                item {
                    AudioPlayer(uri = it)
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
                                selected = audioViewModel.audioCodec == it,
                                onClick = { audioViewModel.audioCodec = it },
                                label = { Text(it.name) }
                            )
                        }
                    }
                }
            }
            item {
                Column() {
                    Text("Output File Extension:", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = AudioExtensions.all) {
                            FilterChip(
                                selected = audioViewModel.fileExtension == it,
                                onClick = { audioViewModel.fileExtension = it },
                                label = { Text(it.name) }
                            )
                        }
                    }
                }
            }
            item {
                var reverseAudio by remember {
                    mutableStateOf(audioViewModel.reverseData?.audio ?: false)
                }
                ToggleSwitchRow(
                    title = "Reverse Audio:",
                    checked = reverseAudio,
                    onCheckedChange = {
                        reverseAudio = it
                        audioViewModel.reverseData =
                            ReverseData(audio = reverseAudio, video = false)
                    })
            }
            audioViewModel.ffmpegStatus?.let {
                item {
                    ConverterStatusDisplay(status = it)
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { audioViewModel.startConverter(context) }) {
                        Text(text = "Start")
                    }
                }
            }
        }
    }
}
