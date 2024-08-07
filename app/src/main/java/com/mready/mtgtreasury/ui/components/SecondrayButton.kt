package com.mready.mtgtreasury.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    content: @Composable BoxScope.() -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp,Color.White.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}