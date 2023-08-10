package app.suhasdissa.clipframes

sealed class Destination(val route: String) {
    object Home : Destination("home")
    object AudioTool : Destination("audio")
    object VideoTool : Destination("video")
}