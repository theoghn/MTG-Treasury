package com.mready.mtgtreasury.ui.decks

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.ui.components.DeckBox
import com.mready.mtgtreasury.ui.theme.AccentColor

@Composable
fun DecksScreen(
    viewModel: DecksViewModel = hiltViewModel()
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 12.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DeckBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .aspectRatio(0.70f)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, Color.White, CircleShape),
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Text(
                            text = "New Deck",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        items(10) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                DeckBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .aspectRatio(0.70f)
                ) {

                }

                Text(
                    text = "Amalia Life",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}