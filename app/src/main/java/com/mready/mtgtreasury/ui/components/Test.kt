package com.mready.mtgtreasury.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mready.mtgtreasury.ui.theme.AccentColor

@Composable
fun Test(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(AccentColor),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "#1",
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun Prev() {
    Test()
}