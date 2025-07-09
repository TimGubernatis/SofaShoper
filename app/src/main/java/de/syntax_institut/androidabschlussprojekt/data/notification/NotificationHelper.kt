package de.syntax_institut.androidabschlussprojekt.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.syntax_institut.androidabschlussprojekt.R

object NotificationHelper {
    const val CHANNEL_ID = "offers_channel"
    const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Angebote"
            val desc = "Benachrichtigungen für Angebote und Aktionen"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showProgress(context: Context, progress: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Angebote werden geladen…")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .setOngoing(true)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun showSuccess(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Glückwunsch!")
            .setContentText("Sie bekommen heute auf alles 10%!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(false)
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
} 