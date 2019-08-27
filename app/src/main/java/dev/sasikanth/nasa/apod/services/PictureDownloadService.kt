package dev.sasikanth.nasa.apod.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.Coil
import coil.api.get
import dev.sasikanth.nasa.apod.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class PictureDownloadService :
    Service(), CoroutineScope {

    companion object {
        private const val KEY_PICTURE_NAME = "dev.sasikanth.nasa.apod.picture_name"
        private const val KEY_PICTURE_DOWNLOAD_URL = "dev.sasikanth.nasa.apod.download_picture"
        private const val CHANNEL_ID = "Downloads"

        fun startService(context: Context, pictureName: String, downloadUrl: String?) {
            val intent = Intent(context, PictureDownloadService::class.java)
            intent.putExtra(KEY_PICTURE_NAME, pictureName)
            intent.putExtra(KEY_PICTURE_DOWNLOAD_URL, downloadUrl)
            ActivityCompat.startForegroundService(context, intent)
        }
    }

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intentNullable: Intent?, flags: Int, startId: Int): Int {
        intentNullable?.let { intent ->
            val pictureName = intent.getStringExtra(KEY_PICTURE_NAME)
            val downloadUrl = intent.getStringExtra(KEY_PICTURE_DOWNLOAD_URL)

            if (downloadUrl == null) {
                stopSelf()
            } else {
                launch {
                    downloadPicture(pictureName, downloadUrl)
                }
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun downloadPicture(pictureName: String?, downloadUrl: String) {
        try {
            // If hd image is already loaded by Coil, it will instantly get the image
            // from disk cache which can be saved to file.
            val drawable: BitmapDrawable = Coil.get(downloadUrl) as BitmapDrawable
            val timeStamp = Calendar.getInstance().timeInMillis
            val title = pictureName?.replace(" ", "_")?.toLowerCase() ?: "GAZE_$timeStamp"

            // Since Environment.getExternalStoragePublicDirectory() is deprecated with Q
            // decided to go for saving with MediaStore
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.TITLE, title)
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, title)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, timeStamp)
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, timeStamp)

            val insertUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (insertUri != null) {
                contentResolver.openOutputStream(insertUri)?.use {
                    drawable.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(getString(R.string.notification_download_desc_success))
                    .setSmallIcon(R.drawable.ic_cloud_download)

                NotificationManagerCompat.from(applicationContext).apply {
                    notify(Random.nextInt(), builder.build())
                }
                stopSelf()
            }
        } catch (e: Exception) {
            Timber.e(e)
            stopSelf()
        }
    }

    private fun startForeground() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_download_title))
            .setContentText(getString(R.string.notification_download_desc))
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setProgress(0, 100, true)

        startForeground(Random.nextInt(), builder.build())
    }

    private fun createNotificationChannel() {
        // Creates NotificationChannel on API26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
