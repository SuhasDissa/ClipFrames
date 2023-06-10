package app.suhasdissa.clipframes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.suhasdissa.clipframes.ui.screens.HomeScreen
import app.suhasdissa.clipframes.ui.screens.audiotools.AudioConverterScreen
import app.suhasdissa.clipframes.ui.screens.audiotools.AudioReverseScreen
import app.suhasdissa.clipframes.ui.screens.videotools.ExtractAudioScreen
import app.suhasdissa.clipframes.ui.screens.videotools.TrimMode
import app.suhasdissa.clipframes.ui.screens.videotools.VideoConverterScreen
import app.suhasdissa.clipframes.ui.screens.videotools.VideoReverseScreen
import app.suhasdissa.clipframes.ui.screens.videotools.SpeedAdjustScreen
import app.suhasdissa.clipframes.ui.screens.videotools.VideoTrimmerScreen

@Composable
fun AppNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Home.route
    ) {
        composable(route = Home.route) {
            HomeScreen(onClickCard = { route ->
                navHostController.navigateTo(route)
            })
        }
        // Video Tools
        composable(route = VideoTools.Converter.route) {
            VideoConverterScreen()
        }
        composable(route = VideoTools.AdjustSpeed.route) {
            SpeedAdjustScreen(audioOnly = false)
        }
        composable(route = VideoTools.Reverse.route) {
            VideoReverseScreen()
        }
        composable(route = VideoTools.ExtractAudio.route) {
            ExtractAudioScreen()
        }
        composable(route = VideoTools.Trimmer.route) {
            VideoTrimmerScreen(trimMode = TrimMode.VIDEO)
        }
        composable(route = VideoTools.GIF.route) {
            VideoTrimmerScreen(trimMode = TrimMode.GIF)
        }

        // Audio Tools
        composable(route = AudioTools.Converter.route) {
            AudioConverterScreen()
        }
        composable(route = AudioTools.Reverse.route) {
            AudioReverseScreen()
        }
        composable(route = AudioTools.AdjustSpeed.route) {
            SpeedAdjustScreen(audioOnly = true)
        }
    }
}

fun NavHostController.navigateTo(route: String) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}