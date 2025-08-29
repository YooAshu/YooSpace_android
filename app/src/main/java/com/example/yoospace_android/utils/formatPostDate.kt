package com.example.yoospace_android.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatPostDate(createdAt: String): String {
    return try {
        // Parse MongoDB date (UTC) and convert to system timezone
        val instant = Instant.parse(createdAt)
        val postTime = instant.atZone(ZoneId.systemDefault())
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val minutesAgo = ChronoUnit.MINUTES.between(postTime, now)
        val hoursAgo = ChronoUnit.HOURS.between(postTime, now)
        val daysAgo = ChronoUnit.DAYS.between(postTime, now)

        when {
            minutesAgo < 1 -> "Just now"
            minutesAgo < 60 -> "${minutesAgo}m ago"
            hoursAgo < 24 -> "${hoursAgo}h ago"
            daysAgo < 7 -> "${daysAgo}d ago"
            else -> postTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
        }
    } catch (_: Exception) {
        createdAt // fallback
    }
}
