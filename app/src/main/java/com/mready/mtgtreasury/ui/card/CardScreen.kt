package com.mready.mtgtreasury.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.card.CardLegalities
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.models.card.formatReleaseDate
import com.mready.mtgtreasury.ui.components.AsyncSvg
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.home.DescriptionField
import com.mready.mtgtreasury.ui.theme.BottomBarColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.LegalChipColor
import com.mready.mtgtreasury.ui.theme.NotLegalChipColor
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel(),
    id: String,
    onBack: () -> Boolean
) {
    val uiState by viewModel.uiState.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(key1 = id) {
//        viewModel.getCard("4ec318c6-b718-436f-b9e8-e0c6154e5010")
        viewModel.getCard(id)
    }
    Box(modifier = modifier.fillMaxSize()) {
        when (val currentState = uiState) {
            is CardScreenUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BoxColor)
                )
            }

            is CardScreenUiState.CardUi -> {
                val card = currentState.mtgCard

                BottomSheetScaffold(
                    modifier = Modifier.fillMaxWidth(),
                    sheetPeekHeight = screenHeight * 2 / 5,
                    scaffoldState = scaffoldState,
                    sheetContent = {
                        card?.let { SheetContent(card = card, screenHeight = screenHeight) }
                    },
                    containerColor = BoxColor,
                    sheetContainerColor = BottomBarColor
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .background(BoxColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PrimaryButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(40.dp)
                                    .clip(CircleShape),
                                onClick = { onBack() },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            PrimaryButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .size(40.dp)
                                    .clip(CircleShape),
                                onClick = { },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }


                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = card?.imageUris?.borderCrop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 54.dp, vertical = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Transparent)
                                    .align(Alignment.TopCenter),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
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
            .padding(bottom = 30.dp)
            .padding(horizontal = 20.dp)
            .heightIn(max = screenHeight - 72.dp - 30.dp)
            .verticalScroll(state = scrollState)
    ) {
        Text(
            text = card.name,
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            card.manaCost.split("{", "}").forEach { color ->
                println(color)
                AsyncSvg(
                    modifier = Modifier.size(18.dp),
                    uri = "https://svgs.scryfall.io/card-symbols/$color.svg"
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.Start),
            text = card.type,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )

        OracleText(
            modifier = Modifier
                .padding(bottom = 8.dp),
            oracleText = card.oracleText
        )

        OracleText(
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

        DescriptionField(
            firstPart = stringResource(R.string.text_artist),
            secondPart = card.artist,
            secondPartColor = Color.LightGray
        )

        DescriptionField(
            firstPart = stringResource(R.string.text_rank),
            secondPart = card.edhRank.toString(),
            secondPartColor = Color.LightGray
        )

        DescriptionField(
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