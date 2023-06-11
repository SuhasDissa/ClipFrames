package app.suhasdissa.clipframes.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.suhasdissa.clipframes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithFAB(
    @StringRes title: Int,
    onFileOpen: () -> Unit,
    onAction: () -> Unit,
    actionAllowed: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(title),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(onClick = onFileOpen) {
                    Icon(
                        imageVector = Icons.Default.FileOpen,
                        contentDescription = stringResource(R.string.open_file)
                    )
                }
                AnimatedVisibility(visible = actionAllowed) {
                    FloatingActionButton(onClick = onAction) {
                        Icon(
                            imageVector = Icons.Default.DoubleArrow,
                            contentDescription = stringResource(R.string.start_processing)
                        )
                    }
                }
            }
        },
        content = content
    )
}