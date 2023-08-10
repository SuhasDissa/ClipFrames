package app.suhasdissa.clipframes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.suhasdissa.clipframes.ui.screens.HomeScreen
import app.suhasdissa.clipframes.ui.screens.audiotools.AudioEditorScreen
import app.suhasdissa.clipframes.ui.screens.videotools.VideoEditorScreen

@Composable
fun AppNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.Home.route
    ) {
        composable(route = Destination.Home.route) {
            HomeScreen(onNavigate = {
                navHostController.navigateTo(it.route)
            })
        }

        composable(route = Destination.AudioTool.route) {
            AudioEditorScreen()
        }

        composable(route = Destination.VideoTool.route) {
            VideoEditorScreen()
        }
    }
}

fun NavHostController.navigateTo(route: String) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}
