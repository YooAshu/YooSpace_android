package com.example.yoospace_android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.yoospace_android.R

fun showNotification(type: String, message: String) {
    val context = AppContext.get() // if inside Activity, else pass from constructor in a Manager class
//    Log.d("SocketManager", "🔔 Showing notification: $message")
    val channelId = "app_notifications"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // ✅ Create notification channel (for Android 8+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "App Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    // 🔥 Customize title based on type
    val title = when (type) {
        "follow" -> "New Follower"
        "group_invite" -> "Group Invite"
        "reaction" -> "New Reaction"
        "comment" -> "New Comment"
        else -> "Notification"
    }

    // ✅ Build notification
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher) // replace with your app icon
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    // Show notification with unique ID
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}
