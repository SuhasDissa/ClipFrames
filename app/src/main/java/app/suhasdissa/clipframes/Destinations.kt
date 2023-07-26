package app.suhasdissa.clipframes

interface Destinations {
    val route: String
}

object Home : Destinations {
    override val route = "home"
}

sealed class VideoTools {
    object Converter : Destinations {
        override val route = "video/converter"
    }

    object ExtractAudio : Destinations {
        override val route = "video/ex_audio"
    }

    object Reverse : Destinations {
        override val route = "video/reverse"
    }

    object AdjustSpeed : Destinations {
        override val route = "video/speed"
    }

    object Trimmer : Destinations {
        override val route = "video/trimmer"
    }

    object GIF : Destinations {
        override val route = "video/gif"
    }
}

sealed class AudioTools {
    object Converter : Destinations {
        override val route = "audio/converter"
    }

    object Reverse : Destinations {
        override val route = "audio/reverse"
    }

    object AdjustSpeed : Destinations {
        override val route = "audio/speed"
    }
}
