package com.theoghn.mtgtreasury.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun DeckBox(
    modifier: Modifier = Modifier,
    composable: @Composable BoxScope.() -> Unit = {}
) {
    val color = Color.LightGray
    val shimmerColors = listOf(
        color.copy(alpha = 0.6f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = 600F, y = 600F)
    )

    Box(
        modifier = modifier
            .background(brush),
        contentAlignment = Alignment.Center
    ){
        composable()
    }
}