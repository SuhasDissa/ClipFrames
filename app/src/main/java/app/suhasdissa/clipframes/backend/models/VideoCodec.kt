package app.suhasdissa.clipframes.backend.models

data class VideoCodec(val codec: String, val name: String)

object VideoCodecs {
    val all = listOf(
        VideoCodec("libx264", "x264"),
        VideoCodec("libx265", "x265"),
        VideoCodec("flv", "FLV"),
        VideoCodec("gif", "GIF"),
        VideoCodec("mpeg4", "MPEG4")
    )
}

sealed class x264Preset(val preset: String) {
    object ULTRAFAST : x264Preset("ultrafast")
    object SUPERFAST : x264Preset("superfast")
    object VERYFAST : x264Preset("veryfast")
    object FASTER : x264Preset("faster")
    object FAST : x264Preset("fast")
    object MEDIUM : x264Preset("medium")
    object SLOW : x264Preset("slow")
    object SLOWER : x264Preset("slower")
    object SLOWEST : x264Preset("veryslow")
}