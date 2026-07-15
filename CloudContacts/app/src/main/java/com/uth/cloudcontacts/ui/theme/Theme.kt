package com.uth.cloudcontacts.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5D4037),
    secondary = Color(0xFF8D6E63),
    background = Color(0xFFF5F5DC),
    surface = Color(0xFFF5F5DC)
)

@Composable
fun CloudContactsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
