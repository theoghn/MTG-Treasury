package com.mready.mtgtreasury.ui.cardslist.wishlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.cardslist.InventoryScreenUiState
import com.mready.mtgtreasury.ui.cardslist.InventoryViewModel
import com.mready.mtgtreasury.ui.cardslist.SearchTextField
import com.mready.mtgtreasury.ui.components.CardsGrid
import com.mready.mtgtreasury.ui.search.filter.FilterSearchShimmerScreen
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor


@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel = hiltViewModel(),
    userId: String,
    onNavigateToCard: (String) -> Unit,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(userId) {
        viewModel.initialize(userId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            Column {
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
                        text = stringResource(R.string.wishlist),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }

                SearchTextField(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 2.dp),
                    fieldValue = searchQuery,
                    showBorder = false,
                    color = BoxColor,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    placeholderText = stringResource(id = R.string.search_cards),
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = {
                        keyboardController?.hide()
                    }
                )
            }

        },
        containerColor = Color.Transparent
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            when (val currentState = uiState) {
                is WishlistScreenUiState.WishlistUi -> {
                    val cards = currentState.cards
                    CardsGrid(
                        cards = cards,
                        onNavigateToCard = { id -> onNavigateToCard(id) }
                    )
                }

                is WishlistScreenUiState.Loading -> {
                    FilterSearchShimmerScreen(
                        modifier = Modifier
                            .padding(top = 18.dp)
                    )
                }

                is WishlistScreenUiState.Empty -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.sad_face),
                                fontSize = 50.sp,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.no_cards_found),
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}