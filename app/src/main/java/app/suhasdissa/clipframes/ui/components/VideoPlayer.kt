package app.suhasdissa.clipframes.ui.components

import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.backend.util.PlayerState
import app.suhasdissa.clipframes.backend.util.isPlayingState
import app.suhasdissa.clipframes.backend.util.playPause
import app.suhasdissa.clipframes.backend.util.positionAndDurationState
import app.suhasdissa.clipframes.backend.viewmodels.PlayerViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayer(
    uri: Uri,
    playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
) {
    DisposableEffect(Unit) {
        with(playerViewModel.player) {
            val mediaItem = MediaItem.Builder().setUri(uri).build()
            setMediaItem(mediaItem)
            prepare()
            onDispose {
                stop()
            }
        }
    }
    Column(Modifier.height(500.dp), verticalArrangement = Arrangement.SpaceEvenly) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(factory = { context ->
                PlayerView(context).apply {
                    player = playerViewModel.player
                    useController = false
                }
            }, modifier = Modifier.fillMaxSize())
        }
        PlayerController(playerViewModel.player)
    }
}

@Composable
fun PlayerController(exoPlayer: ExoPlayer) {
    with(exoPlayer) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val positionAndDuration by positionAndDurationState()
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(DateUtils.formatElapsedTime(positionAndDuration.first / 1000))
                var tempSliderPosition by remember { mutableStateOf<Float?>(null) }
                Slider(
                    modifier = Modifier.weight(1f),
                    value = tempSliderPosition ?: positionAndDuration.first.toFloat(),
                    onValueChange = { tempSliderPosition = it },
                    valueRange = 0f.rangeTo(
                        positionAndDuration.second?.toFloat() ?: Float.MAX_VALUE
                    ),
                    onValueChangeFinished = {
                        tempSliderPosition?.let {
                            exoPlayer.seekTo(it.toLong())
                        }
                        tempSliderPosition = null
                    }
                )
                Text(
                    positionAndDuration.second?.let { DateUtils.formatElapsedTime(it / 1000) }
                        ?: ""
                )
            }
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                shape = CircleShape
            ) {
                val playState by isPlayingState()
                IconButton(
                    onClick = {
                        playPause()
                    }
                ) {
                    when (playState) {
                        PlayerState.Buffer -> {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }

                        PlayerState.Play -> {
                            Icon(
                                Icons.Default.Pause,
                                contentDescription = "Pause"
                            )
                        }

                        PlayerState.Pause -> {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play"
                            )
                        }
                    }
                }
            }
        }
    }
}