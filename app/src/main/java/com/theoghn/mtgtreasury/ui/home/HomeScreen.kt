package com.theoghn.mtgtreasury.ui.home

import android.icu.text.NumberFormat
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.models.MtgSet
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.ui.components.PrimaryButton
import com.theoghn.mtgtreasury.ui.components.ShimmerBox
import com.theoghn.mtgtreasury.ui.components.TwoColorText
import com.theoghn.mtgtreasury.ui.theme.AccentColor
import com.theoghn.mtgtreasury.ui.theme.BoxColor
import com.theoghn.mtgtreasury.ui.theme.MainBackgroundColor
import com.theoghn.mtgtreasury.ui.theme.ShimmerColor
import com.theoghn.mtgtreasury.utility.formatPrice
import com.theoghn.mtgtreasury.utility.formatReleaseDate
import com.theoghn.mtgtreasury.utility.getNumberOfLegalFormats
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit,
    navigateToWebView: (String) -> Unit,
    animatedVisibilityScope: AnimatedContentScope
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    when (val currentState = uiState) {
        is HomeScreenUiState.Loading -> {
            HomeShimmer()
        }

        is HomeScreenUiState.HomeUi -> {
            val card = currentState.mtgCard
            val mostValuableCards = currentState.mostValuableCards
            val newestSets = currentState.newestSets
            val inventoryValue = currentState.inventoryValue

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MainBackgroundColor)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                CollectionValue(inventoryValue = inventoryValue)

                CardOfTheDay(card, animatedVisibilityScope, onCardClick)

                MostValuableCards(mostValuableCards, animatedVisibilityScope, onCardClick)

                NewestSets(
                    newestSets = newestSets,
                    navigateToWebView = navigateToWebView
                )
            }
        }
    }
}

@Composable
private fun NewestSets(
    newestSets: List<MtgSet>,
    navigateToWebView: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 32.dp),
            text = stringResource(R.string.home_newest_sets),
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }

    LazyRow(modifier = Modifier.padding(bottom = 40.dp)) {
        items(newestSets) { set ->
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .width(250.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable { navigateToWebView(set.infoUri) }
                    .background(BoxColor)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    modifier = Modifier.size(60.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(set.iconUri)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.card_back),
                    error = painterResource(id = R.drawable.card_back)
                )

                Column {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = set.name,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    TwoColorText(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        firstPart = stringResource(R.string.text_cards),
                        secondPart = "${set.cardCount}"
                    )

                    TwoColorText(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        firstPart = stringResource(R.string.text_release),
                        secondPart = set.releaseDate.formatReleaseDate()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MostValuableCards(
    mostValuableCards: List<MtgCard>,
    animatedVisibilityScope: AnimatedContentScope,
    onCardClick: (String) -> Unit
) {
    Column {
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 20.dp, start = 32.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.home_most_valuable_cards),
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(12.dp))
                .background(BoxColor)
        ) {
            mostValuableCards.forEachIndexed { index, mtgCard ->
                ValuableCardItem(
                    onCardClick = onCardClick,
                    card = mtgCard,
                    index = index,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ValuableCardItem(
    card: MtgCard,
    index: Int,
    animatedVisibilityScope: AnimatedContentScope,
    onCardClick: (String) -> Unit
) {
    Log.d("HomeScreen", "ValuableCardItem-> id: ${card.id}")
    Row(
        modifier = Modifier
            .clickable { onCardClick(card.id) }
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#${index + 1}",
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(card.imageUris.borderCrop)
                .crossfade(true)
                .size(800, 1150)
                .diskCacheKey(card.id)
                .memoryCacheKey(card.id)
                .build(),
            modifier = Modifier
                .padding(start = 12.dp)
                .sharedElement(
                    rememberSharedContentState(
                        key = card.id
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .width(100.dp)
                .aspectRatio(2 / 3f)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.FillBounds,
            contentDescription = card.id,
            placeholder = painterResource(id = R.drawable.card_back),
            error = painterResource(id = R.drawable.card_back)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = card.name,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                color = Color.LightGray,
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardSetName(
                    setName = card.setName,
                    setAbbreviation = card.setAbbreviation,
                    fontSize = 10,
                )
            }

            Text(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                text = formatPrice(card.prices.eur.toDouble()),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentColor,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CardOfTheDay(
    card: MtgCard,
    animatedVisibilityScope: AnimatedContentScope,
    onCardClick: (String) -> Unit
) {
    Column {
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 20.dp, start = 32.dp)
                .align(Alignment.Start),
            text = stringResource(id = R.string.home_card_of_the_day),
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(265.dp)
                .clip(shape = RoundedCornerShape(12.dp))
                .background(BoxColor),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card.imageUris.borderCrop)
                    .crossfade(true)
                    .size(800, 1150)
                    .diskCacheKey(card.id)
                    .memoryCacheKey(card.id)
                    .build(),
                modifier = Modifier
                    .padding(12.dp)
                    .aspectRatio(2 / 3f)
                    .sharedElement(
                        rememberSharedContentState(
                            key = card.id
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .width(169.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        onCardClick(card.id)
                    },
                contentScale = ContentScale.FillBounds,
                contentDescription = card.id,
                placeholder = painterResource(id = R.drawable.card_back),
                error = painterResource(id = R.drawable.card_back)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = card.name,
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentColor,
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CardSetName(
                            setName = card.setName,
                            setAbbreviation = card.setAbbreviation,
                            fontSize = 10,
                        )
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                        text = formatPrice(card.prices.eur.toDouble()),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )

                    TwoColorText(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        firstPart = stringResource(R.string.text_rank),
                        secondPart = "${card.edhRank}"
                    )

                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.text_foil),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.LightGray,
                        )

                        Image(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(
                                id = if (card.foil) {
                                    R.drawable.icons8_checkmark_48
                                } else {
                                    R.drawable.icons8_cancel_48
                                }
                            ),
                            contentDescription = null
                        )
                    }

                    TwoColorText(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        firstPart = stringResource(R.string.text_legal),
                        secondPart = stringResource(
                            R.string.x_max_set,
                            card.getNumberOfLegalFormats()
                        )
                    )

                    TwoColorText(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        firstPart = stringResource(R.string.text_release),
                        secondPart = card.releaseDate.formatReleaseDate()
                    )
                }

                PrimaryButton(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .align(Alignment.BottomEnd),
                    onClick = { onCardClick(card.id) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Composable
private fun CollectionValue(inventoryValue: Double) {
    Column(
        modifier = Modifier.padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.home_collection_value),
            fontSize = 20.sp,
            color = Color.White,
        )
        Text(
            text = NumberFormat.getCurrencyInstance(Locale.GERMANY).format(inventoryValue),
            fontSize = 40.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun CardSetName(
    setName: String,
    setAbbreviation: String,
    iconSize: Int = 16,
    fontSize: Int = 12
) {
    AsyncImage(
        modifier = Modifier.size(iconSize.dp),
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://svgs.scryfall.io/sets/${setAbbreviation}.svg")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        colorFilter = ColorFilter.tint(Color.White),
        contentDescription = null
    )

    Text(
        modifier = Modifier.padding(horizontal = 4.dp),
        text = setName,
        fontSize = fontSize.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Normal,
        color = Color.LightGray
    )
}

@Composable
fun HomeShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShimmerBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .width(240.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = ShimmerColor
            )

            ShimmerBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .width(130.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = ShimmerColor
            )

            ShimmerBox(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .height(265.dp)
                    .clip(shape = RoundedCornerShape(12.dp)),
                color = ShimmerColor
            )

            ShimmerBox(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(shape = RoundedCornerShape(12.dp)),
                color = ShimmerColor
            )

        }
    }
}