package com.example.yoospace_android.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Pink80 = Color(0xFFFA0000)

val Pink40 = Color(0xFF7D5260)

val DarkExtraColors = ExtraColors(
    btn1 = Color(0xFFFFFFFF),
    cardBackground = Color(0xFF1F1F1F),
    textPrimary = Color.Black,
    textSecondary = Color.White,
    header = Color(0xFF030303),
    listBg = Color(0xFF171717),
    bottomNavBg = Color(0xFFFFFFFF),
    shimmer1 = Color(0xFF2A2A2A),
    shimmer2 = Color(0xFF444444),
    item_bg = Color(0xFF131313)

)

val LightExtraColors = ExtraColors(
    btn1 = Color(0xFF000000),
    cardBackground = Color(0xFFFBFFFF),
    textPrimary = Color.White,
    textSecondary = Color.Black,
    header = Color(0xFFF8F8FF),
    listBg = Color(0xFFF5F5F5),
    bottomNavBg = Color(0xFFFFFFFF),
    shimmer1 = Color(0xFFECE7E7),
    shimmer2 = Color(0xFFC9C1C1),
    item_bg = Color(0xFFF8F8FF)
)


data class ExtraColors(
    val btn1: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val header:Color,
    val listBg: Color,
    val bottomNavBg: Color,
    val shimmer1: Color,
    val shimmer2: Color,
    val item_bg: Color
    // Add more as needed
)

val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(
        btn1 = Color.Unspecified,
        cardBackground = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        header = Color.Unspecified,
        listBg = Color.Unspecified,
        bottomNavBg = Color.Unspecified,
        shimmer1 = Color.Unspecified,
        shimmer2 = Color.Unspecified,
        item_bg = Color.Unspecified
    )
}