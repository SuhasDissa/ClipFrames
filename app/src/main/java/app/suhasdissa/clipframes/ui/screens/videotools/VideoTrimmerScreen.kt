package app.suhasdissa.clipframes.ui.screens.videotools

import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.R
import app.suhasdissa.clipframes.backend.models.FFMPEGStatus
import app.suhasdissa.clipframes.backend.viewmodels.TrimmerViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView


@Composable
fun VideoTrimmerScreen(converterViewModel: TrimmerViewModel = viewModel(), trimMode: TrimMode) {
    val context = LocalContext.current
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .setUsePlatformDiagnostics(false)
            .build()
    }
    var startTimeStamp by remember {
        mutableStateOf<Long?>(null)
    }
    var endTimeStamp by remember {
        mutableStateOf<Long?>(null)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                converterViewModel.setInputFileUri(uri, context)

                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()

            }
        }
    LazyColumn(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        converterViewModel.inputFile?.let {
            item {
                DisposableEffect(
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        factory = {
                            StyledPlayerView(context).apply {
                                player = exoPlayer
                                setShowNextButton(false)
                                setShowPreviousButton(false)
                                setShowFastForwardButton(false)
                                setShowRewindButton(false)
                            }
                        }
                    )

                ) {
                    onDispose { exoPlayer.release() }
                }
            }
        }
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
                            it.format,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        item {
            Button(onClick = { launcher.launch(arrayOf("video/*")) }) {
                Text(stringResource(R.string.open_video))
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { startTimeStamp = exoPlayer.currentPosition / 1000 }) {
                    Text(text = stringResource(R.string.set_start_timestamp))
                }
                Button(
                    onClick = {
                        (exoPlayer.currentPosition / 1000).let {
                            if (it > startTimeStamp!!) {
                                endTimeStamp = it
                            }
                        }
                    },
                    enabled = (startTimeStamp != null)
                ) {
                    Text(text = stringResource(R.string.set_stop_timestamp))
                }
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.start_timestamp))
                Text(
                    startTimeStamp?.let {
                        DateUtils.formatElapsedTime(it)
                    } ?: stringResource(R.string.not_set),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.end_timestamp))
                Text(
                    endTimeStamp?.let {
                        DateUtils.formatElapsedTime(it)
                    } ?: stringResource(R.string.not_set),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        item {
            Button(
                onClick = {
                    converterViewModel.startTrimming(
                        context = context,
                        extension = (if (trimMode == TrimMode.GIF) "gif" else "mp4"),
                        startTimestamp = DateUtils.formatElapsedTime(startTimeStamp!!),
                        endTimeStamp = DateUtils.formatElapsedTime(endTimeStamp!!)
                    )
                },
                enabled = (startTimeStamp != null && endTimeStamp != null)
            ) {
                Text(stringResource(R.string.start_trimming))
            }

        }
        converterViewModel.ffmpegStatus?.let {
            item {
                when (it) {
                    is FFMPEGStatus.Error -> Text("Some error occured")
                    is FFMPEGStatus.Success -> Text("Trimming Success")
                    is FFMPEGStatus.Cancelled -> Text("Trimming Cancelled")
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

enum class TrimMode {
    VIDEO, GIF
}