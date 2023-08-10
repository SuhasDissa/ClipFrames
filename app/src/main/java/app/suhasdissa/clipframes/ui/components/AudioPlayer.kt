package app.suhasdissa.clipframes.ui.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.backend.viewmodels.PlayerViewModel
import com.google.android.exoplayer2.MediaItem

@Composable
fun AudioPlayer(
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
    PlayerController(playerViewModel.player)
}
