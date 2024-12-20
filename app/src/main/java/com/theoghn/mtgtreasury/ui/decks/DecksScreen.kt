package com.theoghn.mtgtreasury.ui.decks

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.ui.components.DeckBox

@Composable
fun DecksScreen(
    viewModel: DecksViewModel = hiltViewModel(),
    onNavigateToDeck: (String) -> Unit,
    onNavigateToDeckCreation: () -> Unit
) {
    val decks by viewModel.decks.collectAsState()
    Log.d("DecksScreen", "deck: ${decks.size}")

    Column(horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.your_decks),
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

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
                            .clickable { onNavigateToDeckCreation() }
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
                                text = stringResource(R.string.new_deck),
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            items(decks) { deck ->
                Column(
                    modifier = Modifier.clickable { onNavigateToDeck(deck.id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DeckBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = RoundedCornerShape(8.dp))
                            .aspectRatio(0.71f)
                    ) {
                        AsyncImage(
                            model = deck.deckImage,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(6.dp))
                                .background(Color.Transparent),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = null,
                            placeholder = painterResource(id = R.drawable.card_back),
                            error = painterResource(id = R.drawable.card_back)
                        )

                    }

                    Text(
                        text = deck.name,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}