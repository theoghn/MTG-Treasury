package com.mready.mtgtreasury.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToCard: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is ProfileScreenUiState.Loading -> {
            // Loading
        }

        is ProfileScreenUiState.ProfileUi -> {
            val user = state.user
            val inventoryCards = state.inventoryCards
            val wishlistCards = state.wishlistCards

            Column(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
//                Image(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .aspectRatio(1f)
//                        .clip(CircleShape),
//                    painter = painterResource(id = R.drawable.awoken_demon_innistrad_midnight_hunt_mtg_art),
//                    contentScale = ContentScale.Crop,
//                    contentDescription = null
//                )
//
//                Text(
//                    text = user.username,
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color.White
//                )
//
//                Row(
//                    modifier = Modifier,
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
////                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    ProfileStat(
//                        title = "Total Value",
//                        value = stringResource(
//                            id = R.string.euro,
//                            String.format("%.2f", user.inventoryValue)
//                        )
//                    )
//
//                    ProfileStat(
//                        title = "Owned",
//                        value = user.inventory.size.toString()
//                    )
//
//                    ProfileStat(
//                        title = "Wishlisted",
//                        value = user.wishlist.size.toString()
//                    )
//                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        painter = painterResource(id = R.drawable.awoken_demon_innistrad_midnight_hunt_mtg_art),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = user.username,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ProfileStat(
                                title = "Total Value",
                                value = stringResource(
                                    id = R.string.euro,
                                    String.format("%.2f", user.inventoryValue)
                                )
                            )

                            ProfileStat(
                                title = "Owned",
                                value = user.inventory.size.toString()
                            )

                            ProfileStat(
                                title = "Wishlisted",
                                value = user.wishlist.size.toString()
                            )
                        }

                    }
                }

                CardsSection(
                    cards = inventoryCards,
                    title = "My Inventory",
                    onCardClick = { onNavigateToCard(it) }
                )

                CardsSection(
                    cards = wishlistCards,
                    title = "My Wishlist",
                    onCardClick = { onNavigateToCard(it) }
                )

                Spacer(modifier = Modifier.weight(1f))


                Button(
                    modifier = Modifier.padding(bottom = 32.dp),
                    onClick = { viewModel.signOut() }
                ) {
                    Text(text = "Sign out")
                }
            }
        }
    }
}

@Composable
fun ProfileStat(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White,
            lineHeight = 13.sp
        )

        Text(
            text = value,
            fontSize = 12.sp,
            color = AccentColor,
            lineHeight = 13.sp
        )
    }
}


@Composable
fun CardsSection(
    modifier: Modifier = Modifier,
    title: String,
    cards: List<MtgCard>,
    onCardClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.White
        )

        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = null,
            tint = Color.White
        )
    }


    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
            .background(BoxColor),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(cards) { card ->
            AsyncImage(
                model = card.imageUris.smallSize,
                modifier = Modifier
                    .padding(4.dp)
                    .width(60.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(Color.Transparent)
                    .clickable { onCardClick(card.id) },
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.card_back),
                error = painterResource(id = R.drawable.card_back)
            )
        }
    }
}