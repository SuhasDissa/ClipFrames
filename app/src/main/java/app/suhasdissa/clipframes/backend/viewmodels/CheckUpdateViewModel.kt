package app.suhasdissa.clipframes.backend.viewmodels

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.suhasdissa.clipframes.ClipFramesApplication

class CheckUpdateViewModel(context: Context) : ViewModel() {
    val currentVersion: Float

    init {
        currentVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            ).versionName.toFloat()
        } else {
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).versionName.toFloat()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ClipFramesApplication) // ktlint-disable max-line-length
                CheckUpdateViewModel(application.applicationContext)
            }
        }
    }
}