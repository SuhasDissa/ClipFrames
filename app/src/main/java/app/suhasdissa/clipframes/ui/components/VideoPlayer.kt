package app.suhasdissa.clipframes.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun VideoPlayer(uri: Uri, context: Context) {
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .setUsePlatformDiagnostics(false)
            .build()
            .also { exoPlayer ->
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
    }

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