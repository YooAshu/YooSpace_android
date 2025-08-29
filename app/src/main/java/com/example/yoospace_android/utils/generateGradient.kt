package com.example.yoospace_android.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun generateGradient(userName: String): Brush {
    fun hashCode(str: String): Int {
        return str.fold(0) { acc, char -> acc + char.code }
    }

    val hash = hashCode(userName)
    val hue1 = (hash % 360).toFloat()
    val hue2 = ((hash * 1.5f) % 360)

    val color1 = Color.hsl(hue1, 0.7f, 0.6f)
    val color2 = Color.hsl(hue2, 0.7f, 0.4f)

    return Brush.linearGradient(
        0.0f to color1,
        1.0f to color2,
        start = Offset.Zero,
        end = Offset.Infinite
    )
}