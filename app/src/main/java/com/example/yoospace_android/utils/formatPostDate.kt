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

@RequiresApi(Build.VERSION_CODES.O)
fun formatMessageDate(createdAt: String): String {
    return try {
        // Parse MongoDB date (UTC) and convert to system timezone
        val instant = Instant.parse(createdAt)
        val messageTime = instant.atZone(ZoneId.systemDefault())
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val minutesAgo = ChronoUnit.MINUTES.between(messageTime, now)
        val hoursAgo = ChronoUnit.HOURS.between(messageTime, now)
        val daysAgo = ChronoUnit.DAYS.between(messageTime, now)
        val monthsAgo = ChronoUnit.MONTHS.between(messageTime, now)
        val yearsAgo = ChronoUnit.YEARS.between(messageTime, now)

        when {
            minutesAgo < 1 -> "Just now"
            minutesAgo < 60 -> "${minutesAgo}m"
            hoursAgo < 24 -> {
                // Check if it's today
                if (messageTime.toLocalDate() == now.toLocalDate()) {
                    messageTime.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH))
                } else {
                    "${hoursAgo}h"
                }
            }
            daysAgo == 1L -> "1d"
            daysAgo < 30 -> "${daysAgo}d"
            monthsAgo < 12 -> {
                val months = monthsAgo.toInt()
                if (months == 1) "1 month ago" else "$months m"
            }
            else -> {
                val years = yearsAgo.toInt()
                if (years == 1) "1 year ago" else "$years y"
            }
        }
    } catch (_: Exception) {
        createdAt // fallback
    }
}
