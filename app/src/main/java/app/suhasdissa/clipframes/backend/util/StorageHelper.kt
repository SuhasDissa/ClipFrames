package app.suhasdissa.clipframes.backend.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

object StorageHelper {
    @SuppressLint("SimpleDateFormat")
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")

    fun getOutputFile(extension: String, fileNamePrefix: String): File {
        val currentTimeMillis = Calendar.getInstance().time
        val currentDateTime = dateTimeFormat.format(currentTimeMillis)
        val currentDate = currentDateTime.split("_").first()
        val currentTime = currentDateTime.split("_").last()

        val fileName = "${fileNamePrefix}_${currentDate}_$currentTime"
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$fileName.$extension"
        )
    }

    fun getOutputFile(
        context: Context,
        extension: String,
        fileNamePrefix: String
    ): DocumentFile {
        val currentTimeMillis = Calendar.getInstance().time
        val currentDateTime = dateTimeFormat.format(currentTimeMillis)
        val currentDate = currentDateTime.split("_").first()
        val currentTime = currentDateTime.split("_").last()

        val fileName = "${fileNamePrefix}_${currentDate}_$currentTime"
        val prefDir =
            context.preferences.getString(SaveDirectoryKey, null)

        val saveDir = when {
            prefDir.isNullOrBlank() -> {
                val dir =
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                DocumentFile.fromFile(dir)
            }

            else -> DocumentFile.fromTreeUri(context, Uri.parse(prefDir))!!
        }
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
        return saveDir.createFile(mimeType, fileName)!!
    }
}
