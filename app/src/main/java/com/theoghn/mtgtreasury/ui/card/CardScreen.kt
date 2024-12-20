package com.theoghn.mtgtreasury.ui.card

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.models.card.CardLegalities
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.ui.components.AsyncSvg
import com.theoghn.mtgtreasury.ui.components.SecondaryButton
import com.theoghn.mtgtreasury.ui.components.TwoColorText
import com.theoghn.mtgtreasury.ui.theme.AccentColor
import com.theoghn.mtgtreasury.ui.theme.BottomBarColor
import com.theoghn.mtgtreasury.ui.theme.BoxColor
import com.theoghn.mtgtreasury.ui.theme.LegalChipColor
import com.theoghn.mtgtreasury.ui.theme.NotLegalChipColor
import com.theoghn.mtgtreasury.utility.Constants
import com.theoghn.mtgtreasury.utility.formatPrice
import com.theoghn.mtgtreasury.utility.formatReleaseDate
import kotlin.reflect.full.memberProperties


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CardScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel(),
    id: String,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(key1 = id) {
        viewModel.getCard(id)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val currentState = uiState) {
            is CardScreenUiState.Loading -> {
                BottomSheetScaffold(
                    modifier = Modifier
                        .fillMaxWidth(),
                    sheetPeekHeight = screenHeight * 2 / 5,
                    scaffoldState = scaffoldState,
                    sheetContent = {
                    },
                    containerColor = BoxColor,
                    sheetContainerColor = BottomBarColor
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .background(BoxColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SecondaryButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(40.dp),
                                onClick = {
                                    onBack()
                                },
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            SecondaryButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(40.dp),
                                onClick = {
                                },
                                shape = CircleShape
                            ) {

                            }
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("cardImgUri")
                                    .crossfade(true)
                                    .placeholderMemoryCacheKey(id)
                                    .memoryCacheKey(id)
                                    .diskCacheKey(id)
                                    .build(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 54.dp, vertical = 16.dp)
                                    .aspectRatio(2 / 3f)
                                    .align(Alignment.TopCenter)
                                    .sharedElement(
                                        rememberSharedContentState(
                                            key = id
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.FillBounds,
                                contentDescription = id,
                                placeholder = painterResource(id = R.drawable.card_back),
                                error = painterResource(id = R.drawable.card_back)
                            )
                        }
                    }
                }
            }

            is CardScreenUiState.CardUi -> {
                val card = currentState.mtgCard
                var isFavorite by rememberSaveable {
                    mutableStateOf(currentState.isWishlisted)
                }
                val qty = currentState.qtyInInventory

                BottomSheetScaffold(
                    modifier = Modifier
                        .fillMaxWidth(),
                    sheetPeekHeight = screenHeight * 2 / 5,
                    scaffoldState = scaffoldState,
                    sheetContent = {
                        SheetContent(card = card, screenHeight = screenHeight)
                    },
                    containerColor = BoxColor,
                    sheetContainerColor = BottomBarColor
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .background(BoxColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SecondaryButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(40.dp),
                                onClick = {
                                    viewModel.updateCardQuantity(card.id, qty)
                                    onBack()
                                },
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            SecondaryButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(40.dp),
                                onClick = {
                                    isFavorite = !isFavorite
                                    viewModel.updateWishlist(card.id, isFavorite)
                                },
                                shape = CircleShape
                            ) {
                                Crossfade(targetState = isFavorite, label = "") {
                                    Icon(
                                        imageVector = if (it) {
                                            Icons.Default.Favorite
                                        } else {
                                            Icons.Default.FavoriteBorder
                                        },
                                        contentDescription = null,
                                        tint = if (it) {
                                            Color.Red
                                        } else {
                                            Color.White
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(card.imageUris.borderCrop)
                                    .crossfade(true)
                                    .placeholderMemoryCacheKey(id)
                                    .diskCacheKey(id)
                                    .memoryCacheKey(id)
                                    .build(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 54.dp, vertical = 16.dp)
                                    .aspectRatio(2 / 3f)
                                    .align(Alignment.TopCenter)
                                    .sharedElement(
                                        rememberSharedContentState(
                                            key = id
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.FillBounds,
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.card_back),
                                error = painterResource(id = R.drawable.card_back)
                            )
                        }
                    }
                }

                InventoryManager(
                    qty = qty,
                    addCardToInventory = { viewModel.addCardToInventory(card.id) },
                    removeCardFromInventory = { viewModel.removeCardFromInventory(card.id) }
                )
            }
        }
    }
}

@Composable
private fun BoxScope.InventoryManager(
    qty: Int,
    addCardToInventory: () -> Unit,
    removeCardFromInventory: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Constants.MainGradient.brush)
            .align(Alignment.BottomCenter)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.in_inventory),
            color = Color.White,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.size(40.dp),
            onClick = { removeCardFromInventory() },
            enabled = qty > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = BoxColor,
                disabledContainerColor = BoxColor.copy(alpha = 0.7f)
            ),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_remove_24),
                contentDescription = null,
                tint = Color.White

            )
        }

        Text(
            text = qty.toString(),
            color = Color.White
        )

        Button(
            modifier = Modifier.size(40.dp),
            onClick = { addCardToInventory() },
            colors = ButtonDefaults.buttonColors(containerColor = BoxColor),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun SheetContent(
    modifier: Modifier = Modifier,
    card: MtgCard,
    screenHeight: Dp
) {
    val scrollState = rememberScrollState()
//    72 dp for the top bar + buttons
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .heightIn(max = screenHeight - 72.dp)
            .verticalScroll(state = scrollState)
    ) {
        Text(
            text = card.name,
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Text(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.Start),
            text = card.type,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            card.manaCost.split("{", "}").forEach { color ->
                if (color.isNotEmpty()) {
                    AsyncSvg(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(18.dp),
                        uri = "https://svgs.scryfall.io/card-symbols/$color.svg"
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = formatPrice(card.prices.eur.toDouble()),
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentColor,
        )

        OracleText(
            modifier = Modifier
                .padding(bottom = 8.dp),
            oracleText = card.oracleText
        )

        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 24.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.text_additional_info),
            fontSize = 14.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold,
        )

        TwoColorText(
            firstPart = stringResource(R.string.text_artist),
            secondPart = card.artist,
            secondPartColor = Color.LightGray
        )

        TwoColorText(
            firstPart = stringResource(R.string.text_rank),
            secondPart = card.edhRank.toString(),
            secondPartColor = Color.LightGray
        )

        TwoColorText(
            firstPart = stringResource(R.string.text_release),
            secondPart = card.releaseDate.formatReleaseDate(),
            secondPartColor = Color.LightGray
        )

        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 24.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.text_legalities),
            fontSize = 14.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold,
        )

        Column(Modifier.fillMaxWidth()) {
            CardLegalities::class.memberProperties.reversed().chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    pair.getOrNull(0)?.let { property ->
                        val value = property.get(card.legalities) as? String ?: "N/A"
                        LegalModeItem(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 20.dp),
                            propertyName = property.name,
                            legal = value
                        )
                    }

                    // Display the second property in the pair (if it exists)
                    pair.getOrNull(1)?.let { property ->
                        val value = property.get(card.legalities) as? String ?: "N/A"
                        LegalModeItem(
                            modifier = Modifier.weight(1f),
                            propertyName = property.name,
                            legal = value
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun OracleText(
    modifier: Modifier = Modifier,
    oracleText: String
) {
    val annotatedText = buildAnnotatedString {
        val parts = oracleText.split(Regex("(?<=\\})|(?=\\{)"))

        parts.forEach { part ->
            when {
                part.startsWith("{") && part.endsWith("}") -> {
                    appendInlineContent(part, "?")
                }

                else -> append(part)
            }
        }
    }
    val inlineContent = mutableMapOf<String, InlineTextContent>()

    oracleText.split(Regex("(?<=\\})|(?=\\{)")).forEach { part ->
        if (part.startsWith("{") && part.endsWith("}")) {
            val color = part.substring(1, part.length - 1)
            inlineContent[part] = InlineTextContent(
                placeholder = Placeholder(
                    width = 12.sp,
                    height = 12.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                ),
                children = {
                    AsyncSvg(
                        modifier = Modifier
                            .padding(1.dp)
                            .fillMaxSize(),
                        uri = "https://svgs.scryfall.io/card-symbols/$color.svg",
                    )
                }
            )
        }
    }

    Text(
        modifier = modifier,
        text = annotatedText,
        inlineContent = inlineContent,
        fontSize = 12.sp,
        color = Color.White,
    )
}

@Composable
fun LegalModeItem(
    modifier: Modifier = Modifier,
    propertyName: String,
    legal: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, end = 8.dp),
            text = propertyName.uppercase(),
            fontSize = 10.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold,
        )

        Box(
            modifier = Modifier
                .width(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (legal == "legal") LegalChipColor else NotLegalChipColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = legal.split("_").joinToString(" ").uppercase(),
                fontSize = 8.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}