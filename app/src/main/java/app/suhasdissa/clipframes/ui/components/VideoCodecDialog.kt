package app.suhasdissa.clipframes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.suhasdissa.clipframes.backend.models.VideoCodec
import app.suhasdissa.clipframes.backend.models.VideoCodecs

@Composable
fun VideoCodecDialog(
    modifier: Modifier = Modifier,
    onSubmitButtonClick: (VideoCodec) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedCodec by remember { mutableStateOf(VideoCodecs.all.first()) }
    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            modifier = modifier.width(300.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = modifier.padding(10.dp)) {
                Text("Select Video Codec")
                Spacer(modifier = modifier.height(10.dp))
                LazyColumn(modifier = modifier.height(500.dp)) {
                    items(items = VideoCodecs.all) {
                        Row(
                            modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    selectedCodec = it
                                })
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = (it == selectedCodec),
                                onClick = {
                                    selectedCodec = it
                                }
                            )
                            Text(
                                text = it.name,
                                modifier = modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = modifier.height(10.dp))
                Button(onClick = {
                    onSubmitButtonClick(selectedCodec)
                    onDismissRequest.invoke()
                }) {
                    Text(text = "Select Video Codec")
                }
            }
        }
    }
}
