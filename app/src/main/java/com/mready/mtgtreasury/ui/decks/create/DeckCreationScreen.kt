package com.mready.mtgtreasury.ui.decks.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BottomBarColor
import com.mready.mtgtreasury.ui.theme.BoxColor


@Composable
fun DeckCreationScreen(
    viewModel: DeckCreationViewModel = hiltViewModel(),
    deckId: String?,
    onBack: () -> Boolean
) {
    val deckName by viewModel.deckName.collectAsState()
    val deckCards by viewModel.deckCards.collectAsState()
    val initialized by viewModel.initialized.collectAsState()

    var isAlertDialogVisible by rememberSaveable { mutableStateOf(false) }

    var dialogMessage by rememberSaveable { mutableStateOf("") }

    val inventoryCards by viewModel.inventoryCards.collectAsState()

    LaunchedEffect(deckId) {
        if (deckId != null) {
            viewModel.initialize(deckId)
        } else {
            viewModel.getInventoryCards()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            DeckCreationTopBar(
                title = if (deckId != null) stringResource(R.string.edit_deck) else stringResource(R.string.create_new_deck),
                onBack = onBack
            )
        },
        containerColor = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(top = 12.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    enabled = initialized,
                    value = deckName,
                    onValueChange = {
                        if (it.length <= 30) {
                            viewModel.updateDeckName(it)
                        }
                    },
                    singleLine = true,
                    label = { Text(stringResource(R.string.deck_name)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AccentColor,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedLabelColor = AccentColor
                    )
                )

                if (!initialized) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = AccentColor
                        )
                    }

                } else {
                    if (inventoryCards.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.add_cards_to_your_X,
                                    "Inventory"
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Text(
                            modifier = Modifier.padding(start = 16.dp).align(Alignment.Start),
                            text = "Add cards from your inventory",
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(inventoryCards) { card ->
                                val warningMessage =
                                    stringResource(id = R.string.max_deck_cards_warning)

                                CardItem(
                                    mtgCard = card,
                                    qty = deckCards[card.id] ?: 0,
                                    onAdd = {
                                        if (deckCards.values.sum() < 60) {
                                            viewModel.addCardToDeck(card.id)
                                            return@CardItem
                                        } else {
                                            dialogMessage = warningMessage
                                            isAlertDialogVisible = true
                                        }
                                    },
                                    onRemove = {
                                        viewModel.removeCardFromDeck(card.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            PrimaryButton(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomCenter),
                onClick = {
                    if (deckName.isEmpty()) {
                        dialogMessage = "Deck name can't be empty"
                        isAlertDialogVisible = true
                        return@PrimaryButton
                    }

                    if (deckId != null) {
                        viewModel.updateDeck(
                            deckId,
                            deckName,
                            deckCards.keys.randomOrNull() ?: "",
                            deckCards
                        )
                    } else {
                        viewModel.createDeck(
                            deckName,
                            deckCards.keys.randomOrNull() ?: "",
                            deckCards
                        )
                    }
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    if (isAlertDialogVisible) {
        MaxCardsAlert(
            onDismissRequest = { isAlertDialogVisible = false },
            dialogMessage = dialogMessage
        )
    }
}

@Composable
fun MaxCardsAlert(
    onDismissRequest: () -> Unit,
    dialogMessage: String,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                contentPadding = PaddingValues(vertical = 14.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = { onDismissRequest() }
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        },
        title = {
            Text(
                text = dialogMessage,
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White
            )
        },
        containerColor = BoxColor
    )
}

@Composable
private fun DeckCreationTopBar(
    title: String,
    onBack: () -> Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = { onBack() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White
        )
    }
}

@Composable
private fun CardItem(
    mtgCard: MtgCard,
    qty: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BoxColor)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = mtgCard.imageUris.smallSize,
            modifier = Modifier
                .padding(start = 12.dp)
                .width(45.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Transparent),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.card_back),
            error = painterResource(id = R.drawable.card_back)
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.65f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = mtgCard.name,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
            )

            Text(
                text = mtgCard.type,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.LightGray,
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        Button(
            modifier = Modifier.size(28.dp),
            onClick = { onRemove() },
            enabled = qty > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = BottomBarColor,
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
            modifier = Modifier.size(28.dp),
            onClick = { onAdd() },
            enabled = qty < mtgCard.qty,
            colors = ButtonDefaults.buttonColors(containerColor = BottomBarColor),
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