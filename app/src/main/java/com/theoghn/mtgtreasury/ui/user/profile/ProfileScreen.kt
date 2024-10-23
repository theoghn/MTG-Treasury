package com.theoghn.mtgtreasury.ui.user.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.theoghn.mtgtreasury.ui.components.PrimaryButton
import com.theoghn.mtgtreasury.ui.theme.AccentColor
import com.theoghn.mtgtreasury.ui.theme.BoxColor
import com.theoghn.mtgtreasury.utility.formatPrice
import com.theoghn.mtgtreasury.utility.getProfilePictureResourceId

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    userId: String,
    navigateToInventory: (String) -> Unit,
    navigateToWishlist: (String) -> Unit,
    navigateToSettings: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
//        viewModel.initialize(userId)
        viewModel.initialize(userId)
    }

    when (val state = uiState.value) {
        is ProfileScreenUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = AccentColor
                )
            }
        }

        is ProfileScreenUiState.ProfileUi -> {
            val user = state.user
            val inventoryCards = state.inventoryCards
            val wishlistCards = state.wishlistCards
            val decks = state.decks
            val isCurrentUser = state.isLocalUser

            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
//                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        text = user.username,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    if (isCurrentUser) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { navigateToSettings() },
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BoxColor)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(100.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape),
                            painter = painterResource(id = getProfilePictureResourceId(user.pictureId)),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileStat(
                                title = stringResource(R.string.total_value),
                                value = formatPrice(user.inventoryValue.toDouble())
                            )

                            ProfileStat(
                                title = stringResource(R.string.owned),
                                value = user.inventory.values.sum().toString()
                            )
                        }

                        Spacer(modifier = Modifier.width(42.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileStat(
                                title = stringResource(R.string.wishlisted),
                                value = user.wishlist.size.toString()
                            )

                            ProfileStat(
                                title = stringResource(R.string.decks),
                                value = decks.size.toString()
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    if (user.bio.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                .align(Alignment.Start),
                            text = user.bio,
                            fontSize = 14.sp,
                            color = Color.White,
                        )
                    }
                }

                LazyVerticalGrid(
                    modifier = Modifier.height(120.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BoxColor),
                            contentAlignment = Alignment.Center

                        ) {
                            Text(
                                text = "Decks",
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 15.sp
                            )
                        }

                    }
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BoxColor),
                            contentAlignment = Alignment.Center

                        ) {
                            Text(
                                text = "Trades",
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }


//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 12.dp)
//                        .fillMaxWidth(),
//                    color = Color.DarkGray,
//                    thickness = 3.dp
//                )

//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),
//                    modifier = Modifier.height(600.dp).padding(2.dp),
//                ) {
//                    item {
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(150.dp)
//                                .padding(2.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(Color(0xFFBBDB9A)),
//                            contentAlignment = Alignment.Center
//
//                        ){
//                            Text("hey")
//                        }
//                    }
//                    item {
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(150.dp)
//                                .padding(2.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(Color(0xFFA69ADA)),
//                            contentAlignment = Alignment.Center
//
//                        ){
//                            Text("hey")
//                        }
//                    }
//                    item {
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(150.dp)
//                                .padding(2.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(Color(0xFFA69ADA)),
//                            contentAlignment = Alignment.Center
//
//                        ){
//                            Text("hey")
//                        }
//                    }
//                    item {
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(150.dp)
//                                .padding(4.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(Color(0xFFBBDB9A)),
//                            contentAlignment = Alignment.Center
//
//                        ){
//                            Text("hey")
//                        }
//                    }
//                }


                NewCardsSection(
                    imageUris = inventoryCards.map { it.imageUris.smallSize },
                    title = stringResource(R.string.inventory),
                    onClick = { navigateToInventory(userId) }
                )

                NewCardsSection(
                    modifier = Modifier.padding(top = 4.dp),
                    imageUris = wishlistCards.map { it.imageUris.smallSize },
                    title = stringResource(R.string.wishlist),
                    onClick = { navigateToWishlist(userId) }
                )

//                CardsSection(
//                    modifier = Modifier
//                        .padding(top = 16.dp),
//                    imageUris = inventoryCards.map { it.imageUris.smallSize },
//                    title = stringResource(R.string.inventory),
//                    onClick = { navigateToInventory(userId) }
//                )
//
//                CardsSection(
//                    modifier = Modifier
//                        .padding(top = 16.dp),
//                    imageUris = wishlistCards.map { it.imageUris.smallSize },
//                    title = stringResource(R.string.wishlist),
//                    onClick = { navigateToWishlist(userId) }
//                )

            }
        }
    }
}

@Composable
fun ProfileStat(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.White,
            lineHeight = 15.sp
        )

        Text(
            text = value,
            fontSize = 15.sp,
            color = AccentColor,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 16.sp
        )
    }
}


@Composable
fun CardsSection(
    modifier: Modifier = Modifier,
    title: String,
    imageUris: List<String>,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
                modifier = Modifier.clickable { onClick() },
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }

        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick() }
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                    .background(BoxColor),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(imageUris) { uri ->
                    AsyncImage(
                        model = uri,
                        modifier = Modifier
                            .padding(4.dp)
                            .width(60.dp)
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(Color.Transparent),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.card_back),
                        error = painterResource(id = R.drawable.card_back)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                    .background(BoxColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.add_cards_to_your_X, title),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun NewCardsSection(
    modifier: Modifier = Modifier,
    title: String,
    imageUris: List<String>,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BoxColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 4.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.White,
                lineHeight = 18.sp
            )

            Icon(
                modifier = Modifier.clickable { onClick() },
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }

        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                contentPadding = PaddingValues(8.dp)
            ) {
                items(imageUris) { uri ->
                    AsyncImage(
                        model = uri,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(60.dp)
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(Color.Transparent),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.card_back),
                        error = painterResource(id = R.drawable.card_back)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                    .background(BoxColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.add_cards_to_your_X, title),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

