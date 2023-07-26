package app.suhasdissa.clipframes.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.suhasdissa.clipframes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightCard(@StringRes name: Int, onClick: () -> Unit) {
    ElevatedCard(
        onClick = { onClick() },
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Text(stringResource(id = name), style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
@Preview
private fun HighlightCardPreview() {
    HighlightCard(name = R.string.app_name, {})
}
