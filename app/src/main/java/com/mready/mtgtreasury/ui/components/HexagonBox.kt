package com.mready.mtgtreasury.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.NavBarAccent
import com.mready.mtgtreasury.ui.theme.NavBarItemUnselected
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun HexagonBox(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val borderColor = NavBarAccent
    var isPressed by remember { mutableStateOf(false) }

    val gradientColors = listOf(Color(0xFFA774FA),Color(0xFF6115DB))

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1f,
        animationSpec = tween(
            durationMillis = 130,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .size(62.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        val x = tryAwaitRelease()
                        if (x) {
                            isPressed = false
                            onClick()
                        } else {
                            isPressed = false
                        }
                    }
                )
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    this.shader = if (isSelected) {
                        LinearGradientShader(
                            from = Offset.Zero,
                            to = Offset(size.width, size.height),
                            colorStops = listOf(0.5f,1f),
                            colors = gradientColors
                        )
                        }
                        else {
                            null
                        }
                    this.color = NavBarItemUnselected
                    this.alpha = if (isSelected) 1f else 1f
                    this.style = PaintingStyle.Fill
                    this.pathEffect = PathEffect.cornerPathEffect(12f)
                }

                canvas.drawPath(drawCustomHexagonPath(size), paint)
            }

            drawPath(
                path = drawCustomHexagonPath(size, scale = 2.5f),
                alpha = if (isSelected) 1f else 0.6f,
                color = if (isSelected) Color.White else borderColor,
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.cornerPathEffect(8f)),
            )
        }
        content()
    }
}

fun drawCustomHexagonPath(size: Size, scale: Float = 2f): Path {
    return Path().apply {
        val radius = min(size.width / scale, size.height / scale)
        customHexagon(radius, size)
    }
}

fun Path.customHexagon(radius: Float, size: Size) {
    val triangleHeight = (sqrt(3.0) * radius / 2)
    val centerX = size.width / 2
    val centerY = size.height / 2

    moveTo(centerX, centerY + radius)
    lineTo((centerX - triangleHeight).toFloat(), centerY + radius / 2)
    lineTo((centerX - triangleHeight).toFloat(), centerY - radius / 2)
    lineTo(centerX, centerY - radius)
    lineTo((centerX + triangleHeight).toFloat(), centerY - radius / 2)
    lineTo((centerX + triangleHeight).toFloat(), centerY + radius / 2)

    close()
}