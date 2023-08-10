package app.suhasdissa.clipframes.ui.screens.videotools

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.clipframes.R
import app.suhasdissa.clipframes.backend.models.TrimTimestamps
import app.suhasdissa.clipframes.backend.viewmodels.FfmpegToolViewModel
import app.suhasdissa.clipframes.backend.viewmodels.PlayerViewModel
import app.suhasdissa.clipframes.ui.components.PlayerController
import com.google.android.exoplayer2.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTrimmerScreen(
    onDismissRequest: () -> Unit,
    converterViewModel: FfmpegToolViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
) {
    var startTimeStamp by remember { mutableStateOf<Long?>(null) }
    var endTimeStamp by remember { mutableStateOf<Long?>(null) }
    val sheetState = rememberModalBottomSheetState(true)
    ModalBottomSheet(onDismissRequest = {
        converterViewModel.trimTimestamps = TrimTimestamps(
            startTimeStamp = DateUtils.formatElapsedTime(startTimeStamp!!),
            endTimeStamp = DateUtils.formatElapsedTime(endTimeStamp!!)
        )
        onDismissRequest.invoke()
    }, sheetState = sheetState) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CenterAlignedTopAppBar(title = {
                Text(
                    "Select Trim Range"
                )
            })
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

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    startTimeStamp = playerViewModel.player.currentPosition / 1000
                }) {
                    Text(text = stringResource(R.string.set_start_timestamp))
                }
                Button(
                    onClick = {
                        (playerViewModel.player.currentPosition / 1000).let {
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.start_timestamp))
                    Text(
                        startTimeStamp?.let {
                            DateUtils.formatElapsedTime(it)
                        } ?: stringResource(R.string.not_set),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column {
                    Text(stringResource(R.string.end_timestamp))
                    Text(
                        endTimeStamp?.let {
                            DateUtils.formatElapsedTime(it)
                        } ?: stringResource(R.string.not_set),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

    }
}