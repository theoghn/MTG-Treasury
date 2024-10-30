package com.theoghn.mtgtreasury.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TwoColorText(
    modifier: Modifier = Modifier,
    firstPart: String,
    secondPart: String,
    secondPartColor: Color = Color.LightGray,
    fontSize: TextUnit = 12.sp
) {
    Text(
        modifier = modifier,
        text =
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = secondPartColor)) {
                append("$firstPart ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            ) {
                append(secondPart)
            }
        },
        fontSize = fontSize,
        fontWeight = FontWeight.Normal,
        color = Color.White
    )
}