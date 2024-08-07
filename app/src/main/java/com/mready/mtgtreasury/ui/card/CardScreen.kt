package com.mready.mtgtreasury.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mready.mtgtreasury.models.card.CardLegalities
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.models.card.formatReleaseDate
import com.mready.mtgtreasury.ui.components.AsyncSvg
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.ShimmerBox
import com.mready.mtgtreasury.ui.home.DescriptionField
import com.mready.mtgtreasury.ui.theme.BottomBarColor
import com.mready.mtgtreasury.ui.theme.BoxColor
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

    LaunchedEffect(key1 = id) {
        viewModel.getCard("4ec318c6-b718-436f-b9e8-e0c6154e5010")
//        viewModel.getCard(id)
    }

    when (val currentState = uiState) {
        is CardScreenUiState.Loading -> {
            ShimmerBox(modifier = Modifier.fillMaxSize(), color = Color.Black)
        }

        is CardScreenUiState.CardUi -> {
            val card = currentState.mtgCard

            BottomSheetScaffold(
                modifier = Modifier.fillMaxWidth(),
                sheetPeekHeight = 350.dp,
                scaffoldState = scaffoldState,
                sheetContent = {
                    card?.let { SheetContent(card = card) }
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

@Composable
fun ColumnScope.SheetContent(
//    modifier: Modifier = Modifier,
    card: MtgCard
) {


    Row(
        modifier = Modifier.padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = card.name,
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

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
            .padding(bottom = 8.dp, start = 20.dp)
            .align(Alignment.Start),
        text = card.type,
        fontSize = 12.sp,
        color = Color.White,
        fontWeight = FontWeight.SemiBold
    )

    OracleText(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        oracleText = card.oracleText
    )

    Text(
        modifier = Modifier
            .padding(bottom = 8.dp, start = 20.dp)
            .align(Alignment.Start),
        text = "Additional Info",
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold
    )

    DescriptionField(
        modifier = Modifier.padding(start = 20.dp),
        key = "Artist",
        value = card.artist,
        keyColor = Color.Gray
    )

    DescriptionField(
        modifier = Modifier.padding(start = 20.dp),
        key = "Rank",
        value = card.edhRank.toString(),
        keyColor = Color.Gray
    )

    DescriptionField(
        modifier = Modifier.padding(start = 20.dp),
        key = "Release",
        value = card.releaseDate.formatReleaseDate(),
        keyColor = Color.Gray
    )

    Column {
        CardLegalities::class.memberProperties.forEach { property ->
            val value = property.get(card.legalities) as? String ?: "N/A"
            Row {
                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 20.dp),
                    text = property.name.uppercase(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )

                LegalChip(legal = value)
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
        color = Color.White
    )
}

@Composable
fun LegalChip(
    modifier: Modifier = Modifier,
    legal: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (legal == "legal") Color.Green else Color.LightGray)
    ) {
        Text(text = legal.split("_").joinToString(" ").uppercase())
    }
}