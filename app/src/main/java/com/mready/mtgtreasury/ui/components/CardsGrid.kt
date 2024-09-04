package com.mready.mtgtreasury.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.utility.formatPrice


@Composable
fun CardsGrid(
    cards: List<MtgCard>,
    onNavigateToCard: (String) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 12.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 32.dp,top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(cards) { mtgCard ->
            MtgCardItem(
                mtgCard = mtgCard,
                onClick = {
                    onNavigateToCard(mtgCard.id)
                },
                isInInventory = mtgCard.qty > 0
            )
        }
    }
}

@Composable
fun MtgCardItem(
    modifier: Modifier = Modifier,
    mtgCard: MtgCard,
    isInInventory: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(280.dp)
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(4.dp)
            ),
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = BoxColor,
            contentColor = BoxColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mtgCard.imageUris.borderCrop)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Transparent)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.FillHeight,
                placeholder = painterResource(id = R.drawable.card_back),
                error = painterResource(id = R.drawable.card_back),
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = mtgCard.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                text = mtgCard.setName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isInInventory) {
                    Text(
                        text = stringResource(R.string.qty, mtgCard.qty),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = formatPrice(mtgCard.prices.eur.toDouble()),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = Color.White
                )
            }
        }
    }

}