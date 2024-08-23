package com.mready.mtgtreasury.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.MtgSet
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.models.card.formatReleaseDate
import com.mready.mtgtreasury.models.card.getNumberOfLegalFormats
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.ShimmerBox
import com.mready.mtgtreasury.ui.components.TwoColorText
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.ShimmerColor

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit
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

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MainBackgroundColor)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                CollectionValue()

                CardOfTheDay(card, onCardClick)

                MostValuableCards(mostValuableCards, onCardClick)

                NewestSets(newestSets)
            }
        }
    }
}

@Composable
private fun NewestSets(newestSets: List<MtgSet>) {
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
                    .clip(shape = RoundedCornerShape(12.dp))
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

@Composable
private fun ColumnScope.MostValuableCards(
    mostValuableCards: List<MtgCard>,
    onCardClick: (String) -> Unit
) {
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
            ValuableCardItem(onCardClick, mtgCard, index)
        }
    }
}

@Composable
private fun ValuableCardItem(
    onCardClick: (String) -> Unit,
    mtgCard: MtgCard,
    index: Int
) {
    Row(
        modifier = Modifier
            .clickable { onCardClick(mtgCard.id) }
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
            model = mtgCard.imageUris.smallSize,
            modifier = Modifier
                .padding(start = 12.dp)
                .width(80.dp)
                .background(Color.Transparent),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
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
                text = mtgCard.name,
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
                    setName = mtgCard.setName,
                    setAbbreviation = mtgCard.setAbbreviation,
                    fontSize = 10,
                )
            }

            Text(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                text = stringResource(id = R.string.euro, mtgCard.prices.eur),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentColor,
            )
        }
    }
}

                @Composable
private fun ColumnScope.CardOfTheDay(
    card: MtgCard,
    onCardClick: (String) -> Unit
) {
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
            model = card.imageUris.borderCrop,
            modifier = Modifier
                .padding(12.dp)
                .width(169.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Transparent),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
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
                    text = stringResource(R.string.euro, card.prices.eur),
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
                        card.getNumberOfLegalFormats() ?: 99
                    )
                )

                TwoColorText(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    firstPart = stringResource(R.string.text_release),
                    secondPart = card.releaseDate.formatReleaseDate()
                        ?: stringResource(R.string.text_unknown)
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

@Composable
private fun CollectionValue() {
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
            text = "$38.46", //TODO get actual value
            fontSize = 40.sp,
            color = Color.White,
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