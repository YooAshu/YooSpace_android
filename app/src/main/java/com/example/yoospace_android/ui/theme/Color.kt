package com.example.yoospace_android.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFF4D12D9)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFFA0000)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val DarkExtraColors = ExtraColors(
    btn1 = Color(0xFFFFFFFF),
    cardBackground = Color(0xFF262626),
    textPrimary = Color.Black,
    textSecondary = Color.White,
    header = Color.Black,
    listBg = Color(0xFF171717)

)

val LightExtraColors = ExtraColors(
    btn1 = Color(0xFF000000),
    cardBackground = Color(0xFF723FF3),
    textPrimary = Color.White,
    textSecondary = Color.Black,
    header = Color.LightGray,
    listBg = Color(0xFFF5F5F5)
)


data class ExtraColors(
    val btn1: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val header:Color,
    val listBg: Color
    // Add more as needed
)

val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(
        btn1 = Color.Unspecified,
        cardBackground = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        header = Color.Unspecified,
        listBg = Color.Unspecified
    )
}