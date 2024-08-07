package com.mready.mtgtreasury.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.mready.mtgtreasury.models.card.formatReleaseDate
import com.mready.mtgtreasury.models.card.getNumberOfLegalFormats
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.ShimmerBox
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor
import com.mready.mtgtreasury.ui.theme.BoxColor

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
            ShimmerBox(modifier = Modifier.fillMaxSize())
        }

        is HomeScreenUiState.HomeUi -> {
            val card = currentState.mtgCard
            val mostValuableCards = currentState.mostValuableCards
            val newestSets = currentState.newestSets

            Column(
                modifier = modifier
                    .padding()
                    .fillMaxSize()
                    .background(MainBackgroundColor)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Current collection value", fontSize = 20.sp, color = Color.White)
                    Text(text = "$38.46", fontSize = 40.sp, color = Color.White)
                }


                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp, top = 20.dp, start = 32.dp)
                        .align(Alignment.Start),
                    text = "Card of the Day",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
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
                        model = card?.imageUris?.borderCrop,
                        modifier = Modifier
                            .padding(12.dp)
                            .width(169.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Transparent),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null
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
                                text = card?.name ?: "Mysterious Card",
                                fontSize = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentColor,
                                textAlign = TextAlign.Center
                            )

                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SetName(
                                    setName = card?.setName ?: "",
                                    setAbbreviation = card?.setAbbreviation ?: "",
                                    fontSize = 10,
                                )
                            }

                            Text(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                                text = "${card?.prices?.eur ?: "0.0"} €",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            DescriptionField(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                key = "Rank",
                                value = "${card?.edhRank ?: 0}"
                            )

                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Foil ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.LightGray
                                )
                                Image(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(
                                        id =
                                        if (card?.foil == true)
                                            R.drawable.icons8_checkmark_48
                                        else
                                            R.drawable.icons8_cancel_48
                                    ),
                                    contentDescription = null
                                )
                            }

                            DescriptionField(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                key = "Legal",
                                value = "${card?.getNumberOfLegalFormats() ?: 99} / 14"
                            )

                            DescriptionField(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                key = "Release",
                                value = card?.releaseDate?.formatReleaseDate() ?: "unknown"
                            )
                        }

                        PrimaryButton(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .align(Alignment.BottomEnd),
                            onClick = { card?.id?.let { onCardClick (it) } },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp, top = 20.dp, start = 32.dp)
                        .align(Alignment.Start),
                    text = "Most Valuable Cards",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(BoxColor)
                ) {
                    mostValuableCards.forEachIndexed { index, mtgCard ->
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
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            AsyncImage(
                                model = mtgCard.imageUris.smallSize,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .width(80.dp)
//                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Transparent),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = null
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
                                    SetName(
                                        setName = mtgCard.setName,
                                        setAbbreviation = mtgCard.setAbbreviation,
                                        fontSize = 10,
                                    )
                                }

                                Text(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                                    text = "${mtgCard.prices.eur} €",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentColor
                                )

                            }
                        }
                    }
                }


                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 32.dp),
                        text = "Newest Sets",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
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
                                contentDescription = null
                            )
                            Column {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = set.name,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                )

                                DescriptionField(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    key = "Cards",
                                    value = "${set.cardCount}"
                                )

                                DescriptionField(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    key = "Release",
                                    value = set.releaseDate.formatReleaseDate()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DescriptionField(
    modifier: Modifier = Modifier,
    key: String,
    value: String,
    keyColor: Color = Color.LightGray,
    fontSize: TextUnit = 12.sp
) {
    Text(
        modifier = modifier,
        text =
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = keyColor)) {
                append("$key ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            ) {
                append(value)
            }
        },
        fontSize = fontSize,
        fontWeight = FontWeight.Normal,
        color = Color.White
    )
}

@Composable
fun SetName(
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



//@Preview
//@Composable
//private fun HomeScreenPreview() {
//    HomeScreen(onCardClick = { navController.navigate(CardScreenDestination)})
//}