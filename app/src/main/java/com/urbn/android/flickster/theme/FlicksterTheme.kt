package com.urbn.android.flickster.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


//<item name="colorPrimary">@color/dark_gray</item>
//<item name="colorPrimaryVariant">@color/black</item>
//<item name="colorOnPrimary">@color/white</item>
//<item name="colorAccent">@color/maroon</item>

private val DarkColorPalette = darkColors(
    primary = Color.DarkGray,
    primaryVariant = Color.Black,
    secondary = Color(0xFF800000), //maroon
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Color.DarkGray,
    primaryVariant = Color.Black,
    secondary = Color(0xFF800000), //maroon
    onPrimary = Color.White,
    onSecondary = Color.White
)

@Composable
fun FlicksterTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}