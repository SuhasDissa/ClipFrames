package app.suhasdissa.clipframes.backend.models

data class AudioCodec(val codec: String, val name: String)
object AudioCodecs {
    val all = listOf(
        AudioCodec("libopus", "OPUS"),
        AudioCodec("aac", "AAC"),
        AudioCodec("libvorbis", "VORBIS")
    )
}
