package app.suhasdissa.clipframes.backend.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class ThumbnailLoader {
    suspend fun loadThumbnails(context: Context, uri: Uri): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val numThumbs = 9
            val thumbSize = 30
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            val interval =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLong()?.times(1000L / numThumbs)!!
            val thumbs = mutableListOf<Deferred<Bitmap?>>()
            for (i in 0 until numThumbs) {
                thumbs.add(async { getThumbnail(i * interval, mediaMetadataRetriever, thumbSize) })
            }
            mediaMetadataRetriever.release()
            thumbs.awaitAll().filterNotNull()
        }
    }

    private suspend fun getThumbnail(
        time: Long,
        mediaMetadataRetriever: MediaMetadataRetriever,
        thumbSize: Int
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                mediaMetadataRetriever.getScaledFrameAtTime(
                    time,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                    thumbSize,
                    thumbSize
                )
            } else {
                mediaMetadataRetriever.getFrameAtTime(
                    time,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                ).let {
                    ThumbnailUtils.extractThumbnail(it, thumbSize, thumbSize)
                }
            }
        }
    }
}