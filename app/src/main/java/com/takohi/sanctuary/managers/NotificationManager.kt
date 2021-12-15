package com.takohi.sanctuary.managers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.takohi.sanctuary.R
import com.takohi.sanctuary.receivers.LockVaultReceiver


class NotificationManager(val context: Context) {
    private val CHANNEL_ID = "main"
    private val LOCK_NOTIFICATION_ID = 1

    @SuppressLint("LaunchActivityFromNotification")
    fun showLockNotification(show: Boolean) {
        if(show) {
            createNotificationChannel()

            val intent = Intent(context, LockVaultReceiver::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_lock_24)
                    .setContentTitle(context.getString(R.string.notification_lock_title))
                    .setContentText(context.getString(R.string.notification_lock_text))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE) // Will be automatically hidden when profile is locked

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(LOCK_NOTIFICATION_ID, builder.build())
            }
        } else {
            with(NotificationManagerCompat.from(context)) {
                cancel(LOCK_NOTIFICATION_ID)
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            with(channel) {
                setSound(null, null)
                enableVibration(false)
            }

            // Register the channel with the system
            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}