package at.markushi.checkmate.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object AppColors {
    val Star = Color(255, 179, 0)
    val Danger = Color(200, 40, 60)
    val Primary = Color(200, 200, 200)

    val Goals = listOf(
        Color(0xFFe91e63.toInt()),
        Color(0xFF673ab7.toInt()),
        Color(0xFF03a9f4.toInt()),
        Color(0xFF009688.toInt()),
        Color(0xFF4caf50.toInt()),
        Color(0xFFffeb3b.toInt()),
        Color(0xFFff5722.toInt()),
        Color(0xFF795548.toInt()),
        Color(0xFF607d8b.toInt())
    )

    val ColorScheme = darkColorScheme(
        primary = Primary
    )
}

fun Int.asColor() = Color(this)