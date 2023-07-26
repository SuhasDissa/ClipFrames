package app.suhasdissa.clipframes.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.suhasdissa.clipframes.AudioTools
import app.suhasdissa.clipframes.R
import app.suhasdissa.clipframes.VideoTools
import app.suhasdissa.clipframes.ui.components.HighlightCard

data class ToolButtonItem(
    @StringRes val name: Int,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onClickCard: (String) -> Unit) {
    val videoTools = listOf(
        ToolButtonItem(R.string.convert_video) { onClickCard(VideoTools.Converter.route) },
        ToolButtonItem(R.string.extract_audio) { onClickCard(VideoTools.ExtractAudio.route) },
        ToolButtonItem(R.string.create_gif) { onClickCard(VideoTools.GIF.route) },
        ToolButtonItem(R.string.reverse_video) { onClickCard(VideoTools.Reverse.route) },
        ToolButtonItem(R.string.change_video_speed) { onClickCard(VideoTools.AdjustSpeed.route) },
        ToolButtonItem(R.string.trim_video) { onClickCard(VideoTools.Trimmer.route) }

    )
    val audioTools = listOf(
        ToolButtonItem(R.string.convert_audio) { onClickCard(AudioTools.Converter.route) },
        ToolButtonItem(R.string.reverse_audio) { onClickCard(AudioTools.Reverse.route) },
        ToolButtonItem(R.string.change_audio_speed) { onClickCard(AudioTools.AdjustSpeed.route) }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(8.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.video_tools),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(items = videoTools) { item ->
                HighlightCard(name = item.name, onClick = item.onClick)
            }

            item {
                Text(
                    stringResource(R.string.audio_tools),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(items = audioTools) { item ->
                HighlightCard(name = item.name, onClick = item.onClick)
            }
        }
    }
}
