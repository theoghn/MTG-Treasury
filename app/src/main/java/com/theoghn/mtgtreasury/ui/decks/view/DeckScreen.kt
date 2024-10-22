package com.theoghn.mtgtreasury.ui.decks.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.models.Deck
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.ui.components.PrimaryButton
import com.theoghn.mtgtreasury.ui.decks.create.MaxCardsAlert
import com.theoghn.mtgtreasury.ui.theme.BottomBarColor
import com.theoghn.mtgtreasury.ui.theme.BoxColor
import com.theoghn.mtgtreasury.utility.formatPrice

@Composable
fun DeckScreen(
    viewModel: DeckViewModel = hiltViewModel(),
    id: String,
    onBack: () -> Boolean,
    navigateToDeckCreation: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteCardDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteDeckDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isMaxCardsDialogVisible by rememberSaveable { mutableStateOf(false) }
    var selectedCardId by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(id) {
        viewModel.getCards(id)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            DeckScreenTopBar(
                deckName = stringResource(R.string.deck_cards),
                onBack = {
                    onBack()
                },
                showDeleteDeck = {
                    isDeleteDeckDialogVisible = true
                }
            )
        },
        containerColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BoxColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val currentState = uiState) {
                    is DeckScreenUiState.DeckUi -> {
                        val deck = currentState.deck
                        val missingCardsIds = currentState.missingCardsIds
                        val cards = currentState.cards
                        var totalValue = 0.0

                        for (card in cards) {
                            totalValue += card.prices.eur.toFloat() * (deck.cards.get(card.id)?: 0)
                        }

                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    text = deck.name,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                )

                                Text(
                                    text = "${deck.cards.values.sum()} / 60 Cards",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "Deck Cost ${formatPrice(totalValue)}",
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(cards) { card ->
                                DeckCardItem(
                                    card = card,
                                    isInInventory = !missingCardsIds.contains(card.id),
                                    qty = deck.cards[card.id] ?: 0,
                                    onClick = {
                                        selectedCardId = card.id
                                        isBottomSheetVisible = true
                                    }
                                )
                            }
                        }

                        if (isBottomSheetVisible) {
                            CardEditBottomSheet(
                                deck = deck,
                                isInInventory = !missingCardsIds.contains(selectedCardId),
                                cardName = cards.find { card -> card.id == selectedCardId }?.name
                                    ?: "",
                                selectedCardId = selectedCardId,
                                removeCard = {
                                    if (deck.cards[selectedCardId] == 1) {
                                        isDeleteCardDialogVisible = true
                                    } else {
                                        viewModel.removeCardFromDeck(selectedCardId)
                                    }
                                },
                                addCard = {
                                    if (deck.cards.values.sum() < 60) {
                                        viewModel.addCardToDeck(selectedCardId)
                                    } else {
                                        isMaxCardsDialogVisible = true
                                    }
                                },
                                showRemoveDialog = { isDeleteCardDialogVisible = true },
                                hideBottomSheet = {
                                    viewModel.updateDeck()
                                    isBottomSheetVisible = false
                                }
                            )
                        }
                    }

                    DeckScreenUiState.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.add_cards_to_your_deck),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }

                    DeckScreenUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            PrimaryButton(
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(100))
                    .align(Alignment.BottomEnd),
                onClick = {
                    viewModel.updateDeck()
                    navigateToDeckCreation(id)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Text(
                        text = stringResource(R.string.edit),
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (isDeleteDeckDialogVisible) {
        RemoveAlert(
            title = stringResource(R.string.warning),
            message = stringResource(R.string.delete_deck_warning),
            hideAlert = { isDeleteDeckDialogVisible = false },
            hideBottomSheet = {
                isBottomSheetVisible = false
            },
            onConfirm = {
                viewModel.deleteDeck()
                onBack()
            }
        )
    }

    if (isDeleteCardDialogVisible) {
        RemoveAlert(
            title = stringResource(R.string.warning),
            message = stringResource(R.string.delete_card_warning),
            hideAlert = { isDeleteCardDialogVisible = false },
            hideBottomSheet = {
                isBottomSheetVisible = false
            },
            onConfirm = {
                viewModel.removeCardFromDeck(selectedCardId, deleted = true)
                viewModel.updateDeck()
            }
        )
    }

    if (isMaxCardsDialogVisible) {
        MaxCardsAlert(
            onDismissRequest = { isMaxCardsDialogVisible = false },
            dialogMessage = stringResource(id = R.string.max_deck_cards_warning)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditBottomSheet(
    cardName: String,
    deck: Deck,
    selectedCardId: String,
    removeCard: () -> Unit,
    addCard: () -> Unit,
    showRemoveDialog: () -> Unit,
    hideBottomSheet: () -> Unit,
    isInInventory: Boolean,
) {
    ModalBottomSheet(
        onDismissRequest = { hideBottomSheet() },
        containerColor = BottomBarColor
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = cardName,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isInInventory) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.this_card_is_not_in_your_inventory),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BoxColor)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.in_deck),
                color = Color.White,
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.size(40.dp),
                onClick = {
                    removeCard()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BoxColor,
                    disabledContainerColor = BoxColor.copy(alpha = 0.7f)
                ),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_remove_24),
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = deck.cards[selectedCardId].toString(),
                color = Color.White
            )

            Button(
                modifier = Modifier.size(40.dp),
                onClick = {
                    addCard()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BoxColor),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            onClick = { showRemoveDialog() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BoxColor),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.delete_card),
                color = Color.Red,
                fontSize = 18.sp,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}


@Composable
fun RemoveAlert(
    title: String,
    message: String,
    confirmText: String = stringResource(R.string.delete),
    hideAlert: () -> Unit,
    hideBottomSheet: () -> Unit = {},
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { hideAlert() },
        title = { Text(text = title, color = Color.White) },
        text = {
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    hideAlert()
                    hideBottomSheet()
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BoxColor),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = confirmText.uppercase(),
                    color = Color.Red,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { hideAlert() },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BoxColor),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)

            ) {
                Text(
                    text = stringResource(id = R.string.cancel).uppercase(),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = BottomBarColor
    )
}


@Composable
fun DeckCardItem(
    card: MtgCard,
    isInInventory: Boolean,
    qty: Int = 0,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = card.imageUris.smallSize,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(4.dp))
                .align(Alignment.Center)
                .background(Color.Transparent)
                .clickable { onClick() },
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.card_back),
            error = painterResource(id = R.drawable.card_back)
        )

        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = qty.toString(),
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (!isInInventory) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.7f))
                    .align(Alignment.BottomStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
        }
    }
}


@Composable
private fun DeckScreenTopBar(
    deckName: String,
    onBack: () -> Boolean,
    showDeleteDeck: () -> Unit
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
            text = deckName,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { showDeleteDeck() }
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}