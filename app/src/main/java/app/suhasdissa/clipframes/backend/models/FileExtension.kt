package app.suhasdissa.clipframes.backend.models

data class FileExtension(
    val extension: String,
    val name: String
)

object AudioExtensions {
    val all = listOf(
        FileExtension("mp3", "MP3"),
        FileExtension("wav", "WAV"),
        FileExtension("aac", "AAC"),
        FileExtension("opus", "OPUS")
    )
}

object VideoExtensions {
    val all = listOf(
        FileExtension("mp4", "MP4"),
        FileExtension("mov", "MOV"),
        FileExtension("mkv", "MKV"),
        FileExtension("flv", "FLV")
    )
}
