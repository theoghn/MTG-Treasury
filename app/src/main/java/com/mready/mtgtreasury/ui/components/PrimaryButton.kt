package com.mready.mtgtreasury.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    content: @Composable BoxScope.() -> Unit
) {
    val gradientColors = listOf(Color(0xFFA774FA), Color(0xFF6115DB))
    val brush = Brush.horizontalGradient(gradientColors)

    Button(
        modifier = modifier.background(brush).heightIn(12.dp).widthIn(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}